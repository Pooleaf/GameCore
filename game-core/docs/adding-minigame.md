# game-core 기반 미니게임 추가 가이드

> **대상 모듈:** `game-core` · **플랫폼:** Paper 1.8.8 (Minecraft 1.8.9) / Kotlin 1.8
> **참고 구현체:** `ability/ability-core` · **작성일:** 2026-05-30

## 목차

1. [개요 — game-core란 무엇인가](#1-개요--game-core란-무엇인가)
2. [핵심 개념 (Game · Phase · PhasePipeline)](#2-핵심-개념)
3. [게임 라이프사이클](#3-게임-라이프사이클)
4. [빠른 시작 — 최소 미니게임 만들기](#4-빠른-시작--최소-미니게임)
5. [단계별 구현](#5-단계별-구현)
6. [커스텀 Phase 작성](#6-커스텀-phase-작성)
7. [Player / PhasePipeline 확장](#7-player--phasepipeline-확장-선택)
8. [설정 · 명령어 · 이벤트](#8-설정--명령어--이벤트)
9. [빌드 · 배포](#9-빌드--배포)
10. [체크리스트 & 자주 하는 실수](#10-체크리스트--자주-하는-실수)

---

## 1. 개요 — game-core란 무엇인가

`game-core`는 풀잎서버 미니게임의 **공통 골격**을 제공하는 프레임워크 플러그인이다. 플레이어·팀·맵·자기장(WorldBorder)·시작 아이템·사이드바·재부팅·투표 등 모든 미니게임이 공유하는 기능을 미리 구현해 두고, 게임마다 달라지는 **"진행 순서(Phase)"만 새 플러그인에서 정의**하도록 설계되어 있다.

따라서 새 미니게임을 만든다는 것은 곧 **game-core에 의존하는 별도 Paper 플러그인을 하나 만들고, 그 안에서 `Game`을 상속한 클래스와 `PhasePipeline`을 구성한 뒤 `GameCore.init()`에 넘겨주는 것**을 의미한다.

이 가이드는 `ability-core`(능력자 미니게임)를 표준 참고 구현체로 삼아, 그 구조를 그대로 따라 새 게임 모듈을 만드는 방법을 단계별로 설명한다.

| game-core 모듈 구성 | 역할 |
|---|---|
| `game-core` | 핵심 프레임워크 (Game, Phase, Player, Team, Map, WorldBorder ...) |
| `game-replay` | 게임 녹화 / 리플레이 (별도 가이드 참조) |
| `game-history` | 게임 기록 저장 |
| `game-reconnector` | 재접속 관리 |
| `game-profile` | 플레이어 프로필 |

---

## 2. 핵심 개념

### Game
> `net/pooleaf/gamecore/game/Game.kt`

미니게임 1종을 나타내는 추상 클래스. `gameTypeId`(게임 구분자)와 `PhasePipeline`을 생성자로 받는다. 게임의 모든 상태 플래그(`isRunning`, `isGameStarted`, `isGodMode` ...)를 보유하지만, 실제 라이프사이클 로직은 `GameManager`에 위임한다.

### Phase
> `net/pooleaf/gamecore/phase/Phase.kt`

게임 진행의 한 단계(시작 카운트, 무적 시간, 자기장 축소, 본게임, 종료 등). `onInit / onStart / onRun / onEnd / onCancel` 생명주기 훅을 가진다. `onRun`이 끝나야 다음 Phase로 넘어간다.

### PhasePipeline
> `net/pooleaf/gamecore/phase/PhasePipeline.kt`

`addPhase()`로 Phase들을 순서대로 쌓는 컨테이너. 게임이 시작되면 비동기로 Phase를 위에서부터 차례대로 실행한다. **미니게임의 "규칙"은 사실상 이 파이프라인 구성**에서 결정된다.

### GameCore
> `net/pooleaf/gamecore/GameCore.kt`

모든 매니저/서비스를 보유한 싱글톤 레지스트리. `GameCore.init(plugin, game)`로 게임을 등록하며, `GameCore.game`, `GameCore.unsafe.playerManager` 등으로 프레임워크 기능에 접근한다.

> ⚠️ **등록 방식 핵심:** game-core는 어노테이션 스캔이나 SPI가 아니라 **"명시적 인스턴스 전달"** 방식을 쓴다. 플러그인이 직접 `Game` 구현체를 `new` 해서 `GameCore.init(this, game)`에 넘긴다. 게임은 서버당 하나만 등록된다.

### game-core가 기본 제공하는 Phase
> `net/pooleaf/gamecore/phases/`

| Phase | 설명 | 비고 |
|---|---|---|
| `StartCountPhase` | 게임 시작 카운트다운 | `open` — 상속 가능 |
| `GodModePhase` | 무적 시간 | `abstract` — `getGodModeSeconds()` 구현 필요 |
| `WorldBorderUpdatePhase` | 자기장 축소 | `abstract` — 목표 크기/시간 구현 필요 |
| `MapTeleportCountPhase` | 맵 텔레포트 카운트 | |
| `GamePhase` | 본게임 진행 (게임 종료까지 대기) | `open` |
| `EndPhase` | 게임 종료 처리 | `open` |
| `DelayPhase(seconds)` | 지정 시간 대기 | 유틸 |
| `RunnablePhase { ... }` | 임의 코드 1회 실행 | 유틸 |

---

## 3. 게임 라이프사이클

`Game`의 메서드는 모두 `GameManager`로 위임된다. 전체 흐름은 다음과 같다.

```
GameCore.init(plugin, game)
        │
        ▼
  game.init()            ──▶  GameManager.initGame()      // 상태 초기화, 게임 등록
  isInitialized = true        GameCoreInitializedEvent 발행
        │
   [관리자가 /게임 시작]
        ▼
  game.start(sender)     ──▶  GameManager.startGame()     // 맵 로드 → PhasePipeline.runPhases()
  isRunning = true            GameStartEvent 발행
        │
        ▼
  ┌─────────── PhasePipeline 순차 실행 ───────────┐
  │  Phase[0].start()  → onStart() → onRun() → onEnd()  │
  │  Phase[1].start()  → ...                            │
  │  ...                                                │
  │  EndPhase                                           │
  └────────────────────────────────────────────────────┘
        │
        ▼
  GameManager.onGameEnd()      // 우승팀 결정, isEnded = true
        │
        ▼
  game.reset()                 // 맵 언로드, 상태 복구 → 다음 게임 준비
```

- **중단(cancel)** 은 `game.cancel(sender, cause)` → `GameManager.cancelGame()`로 처리되며, 진행 중이던 `PhasePipeline.cancelPhases()`가 호출되어 현재 Phase의 `onCancel()`이 실행된다.
- **강제 종료** 가 필요하면 `game.skipToEnd()`로 EndPhase까지 건너뛸 수 있다.

---

## 4. 빠른 시작 — 최소 미니게임

가장 단순한 미니게임은 단 3개의 클래스로 만들 수 있다.

```kotlin
// 1) Game 구현체
class MyGame : Game(
    gameTypeId = 200,                // 게임 고유 ID (ability-core는 100)
    phasePipeline = MyPhasePipeline()
)

// 2) 진행 순서 정의
class MyPhasePipeline : PhasePipeline() {
    init {
        addPhase(StartCountPhase(true))   // 시작 카운트
        addPhase(GamePhase())             // 본게임 (게임 종료까지 대기)
        addPhase(EndPhase())              // 종료 처리
    }
}

// 3) 플러그인 진입점
class MyGamePlugin : BukkitCorePlugin() {
    companion object { lateinit var instance: MyGamePlugin }

    override fun onStart() {
        instance = this
        prefix = "§a[ MyGame ]"
        registerLoggerPrefix()

        GameCore.init(this, MyGame())   // ← 게임 등록 (핵심)

        registerEventListeners()
        registerCommonEventListeners()
        registerCommands()
    }

    override fun onConfigLoaded() {
        GameCore.loadConfig()
    }
}
```

> ✅ 이것만으로 game-core의 모든 공통 기능(`/게임 시작`·`/게임 중단` 명령어, 플레이어 입장/관전 처리, 팀·맵·자기장·시작 아이템·사이드바 등)을 그대로 사용할 수 있다. 이후 게임만의 규칙은 **커스텀 Phase**를 파이프라인에 끼워넣어 추가한다.

---

## 5. 단계별 구현

### 1단계 — 모듈 / 빌드 설정

ability와 동일하게 멀티모듈 구조를 권장한다. `settings.gradle.kts`에 모듈을 추가하고, `build.gradle.kts`에서 game-core를 `compileOnly`로 의존한다.

```kotlin
// settings.gradle.kts
rootProject.name = "my-game"
include("my-game-core")

// 루트 build.gradle.kts (allprojects)
dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc:paper:1.8.8")

    compileOnly("net.pooleaf:core:latest.integration")
    compileOnly("net.pooleaf:game-core:1.9.0")          // ← game-core
    compileOnly("net.pooleaf:permission:latest.integration")
}
```

> ⚠️ **의존성은 반드시 `compileOnly`** 로 둔다. game-core / core 는 서버에 별도 플러그인으로 올라가 있으므로 ShadowJar에 포함하면 안 된다(클래스 중복 로딩 충돌).

### 2단계 — plugin.yml 작성
> `src/main/resources/plugin.yml`

```yaml
name: $pluginName
version: $version
main: $main
depend:
  - Core
  - GameCore       # game-core 플러그인이 먼저 로드되어야 함
```

`$pluginName`·`$version`·`$main`은 `processResources`의 `expand(project.properties)`로 주입된다(루트 빌드 설정에 이미 구성됨).

### 3단계 — Game 구현체
> `net/pooleaf/mygame/game/MyGame.kt`

```kotlin
class MyGame : Game(
    gameTypeId = 200,
    phasePipeline = MyPhasePipeline(),
    waitingGameMode = GameMode.ADVENTURE   // (선택) 대기 중 게임모드, 기본 ADVENTURE
) {
    // 게임 전역에서 공유할 커스텀 상태가 있다면 여기에 둔다.
    // 예) var someFlag: Boolean = false
}
```

> `gameTypeId`는 다른 미니게임과 겹치지 않는 정수를 부여한다(ability-core = 100). 히스토리/리플레이 등에서 게임 종류를 식별하는 데 쓰인다.

### 4단계 — PhasePipeline 구성
> `net/pooleaf/mygame/game/MyPhasePipeline.kt`

게임의 규칙을 시간 순서대로 나열한다. ability-core의 파이프라인을 거의 그대로 가져와도 좋은 출발점이 된다.

```kotlin
class MyPhasePipeline : PhasePipeline() {
    init {
        addPhase(StartCountPhase(true))

        // 게임모드 변경 (한 줄 실행은 RunnablePhase)
        addPhase(RunnablePhase {
            BukkitSyncScope.launch {
                GameCore.game.changeCurrentGameMode(GameMode.SURVIVAL)
            }
        })
        addPhase(DelayPhase(2))

        // 무적 시간 (abstract Phase → 익명 클래스로 구현)
        addPhase(object : GodModePhase() {
            override fun getGodModeSeconds(): Int = 60
        })

        // 자기장 축소
        addPhase(object : WorldBorderUpdatePhase() {
            override fun getNewWorldBorderSize(): Int = 20
            override fun getUpdateWaitSeconds(): Int = 300
            override fun getUpdateSizePerSeconds(): Int = 1
        })

        addPhase(GamePhase())
        addPhase(EndPhase())
    }
}
```

### 5단계 — 플러그인 진입점 + 등록
> `net/pooleaf/mygame/MyGamePlugin.kt`

4번 섹션의 `MyGamePlugin` 코드를 그대로 사용한다. 핵심은 `onStart()` 안에서 `GameCore.init(this, MyGame())`를 호출하는 것이다. 이 호출이 끝나면 `GameCoreInitializedEvent`가 발행되고, game-core 본체가 자신의 명령어/리스너를 등록한다.

> ❗ **순서 주의:** `GameCore.init()`은 다른 game-core API(`GameCore.unsafe.*`, `GameCore.game`)보다 **먼저** 호출되어야 한다. init 이전에는 매니저들이 아직 `lateinit` 상태라 접근 시 예외가 발생한다.

---

## 6. 커스텀 Phase 작성

게임만의 고유 규칙(전용 미션, 특수 이벤트, 점수 집계 등)은 `Phase`를 상속해 구현한다.

```kotlin
class MyEventPhase : Phase() {

    // Phase 진입 시 초기화 (멱등하게)
    override fun onInit() { }

    // 시작 안내 / 셋업 (코루틴)
    override suspend fun onStart() {
        BukkitBroadcaster.broadcast("§e특수 이벤트가 곧 시작됩니다!")
        delay(3000L)
    }

    // 이 Phase의 본 로직. onRun 이 끝나야 다음 Phase로 진행된다.
    override suspend fun onRun() {
        var elapsed = 0
        while (!GameCore.game.isEnded && elapsed < 60) {
            // 매 초 처리
            delay(1000L)
            elapsed++
        }
    }

    // 종료 정리 / 메시지
    override fun onEnd() {
        BukkitBroadcaster.broadcast("§e특수 이벤트 종료!")
    }

    // 게임 중단 시
    override fun onCancel() { }
}
```

> 📌 **onRun 이 곧 흐름 제어다.** `PhasePipeline`은 한 Phase의 `start()`가 완료(=onRun 종료)될 때까지 다음 Phase를 시작하지 않는다. 따라서 "특정 조건까지 머무르는" Phase는 `onRun` 안에서 `while (...) { delay(...) }` 패턴으로 대기시킨다. 대표적으로 `GamePhase`는 `game.isEnded`가 될 때까지 머무른다.

| 훅 | 스레드 | 용도 |
|---|---|---|
| `onInit()` | 동기 | Phase 재사용을 위한 상태 리셋 |
| `onStart()` | 코루틴(비동기) | 시작 메시지, 셋업, 짧은 연출 |
| `onRun()` | 코루틴(비동기) | Phase 본 로직, 진행/대기 루프 |
| `onEnd()` | 동기 | 종료 메시지, 정리 |
| `onCancel()` | 동기 | 게임 중단 시 정리 |

> ⚠️ Bukkit API(엔티티 스폰, 블록 변경 등) 호출은 메인 스레드에서 해야 한다. 코루틴 훅 안에서는 `BukkitSyncScope.launch { ... }`로 메인 스레드에 넘겨 실행한다.

---

## 7. Player / PhasePipeline 확장 (선택)

게임마다 플레이어에게 추가 상태가 필요하면 `GamePlayer`를 상속한다. ability-core가 `AbilityPlayer`로 능력 정보를 붙인 것이 대표 사례다.

```kotlin
// 1) GamePlayer 확장
class MyPlayer(uuid: UUID) : GamePlayer(uuid) {
    var score: Int = 0
}

// 2) Factory + Manager 확장
class MyPlayerFactory : GamePlayerFactory<MyPlayer> {
    override fun createGamePlayer(uuid: UUID) = MyPlayer(uuid)
}
class MyPlayerManager : GamePlayerManager<MyPlayer>(MyPlayerFactory())

// 3) 플러그인 onStart()에서 game-core의 playerManager를 교체
GameCore.init(this, MyGame())
MyApi.init()  // MyPlayerManager 생성
GameCore.unsafe.playerManager =
    MyApi.unsafe.playerManager as GamePlayerManager<GamePlayer>
```

> 실제 ability-core는 `AbilityApi`라는 `object` 싱글톤에 매니저/서비스/설정/게임 인스턴스를 모아두고, `AbilityApi.unsafe.init()`에서 한꺼번에 초기화한다. 규모가 커지면 이 패턴(자체 Api 객체)을 따르는 것이 깔끔하다.

---

## 8. 설정 · 명령어 · 이벤트

### 설정 (Config)

`SimpleAnnoConfig`를 상속하고 `@ConfigName`으로 필드를 노출한다. 파일은 `GameCore.gamePlugin.dataFolder` 아래에 둔다.

```kotlin
class MyGameConfig(file: File?) : SimpleAnnoConfig(file) {
    @ConfigName("무적 시간(초)")
    var godModeSeconds: Int = 60

    @ConfigName("자기장 축소.첫번째.대기시간")
    var firstReduceWaitSeconds: Int = 300
}

// 로드 (보통 자체 Api 객체에서 lazy 로드)
val myGameConfig by lazy {
    MyGameConfig(File(GameCore.gamePlugin.dataFolder, "my-game-config.yml"))
}
fun loadConfig() { myGameConfig.load(); myGameConfig.save() }
```

### 명령어 (Command)

core의 `annocommand` 모듈을 사용한다. `@Command`가 붙은 메서드를 가진 클래스를 만들고, `registerCommands()`가 자동 스캔한다.

```kotlin
class MyGameCommand {
    @Command(parent = ["게임"], name = ["특수", "special"],
             description = "특수 이벤트를 발동합니다.",
             permission = "mygame.admin")
    fun special(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        // ...
    }
}
```

### 이벤트 (Event)

game-core는 게임 상태 변화 시 Bukkit 이벤트를 발행한다(`GameStartEvent`, `GameEndEvent`, `GamePlayerJoinEvent`, `GamePlayerInitEvent`, `TeamDefeatEvent` 등). 일반 Bukkit `Listener`로 구독하면 된다.

```kotlin
class MyGameListener : Listener {
    @EventHandler
    fun onGameStart(event: GameStartEvent) {
        Logger.log("게임이 시작되었습니다.")
    }
}
// onStart() 에서 registerEventListeners() 가 패키지 내 Listener를 자동 등록
```

> `registerEventListeners()` / `registerCommands()`는 `BukkitCorePlugin`이 제공하는 헬퍼로, 플러그인 패키지 내의 `Listener` / `@Command` 클래스를 자동으로 찾아 등록한다. 별도 수동 등록 코드를 작성할 필요가 없다.

---

## 9. 빌드 · 배포

game-core와 동일한 ShadowJar + Nexus publish 구성을 사용한다.

```kotlin
plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
}
tasks {
    withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }
    processResources {
        filesMatching("**/*.yml") { expand(project.properties) }
    }
    withType<ShadowJar> {
        delete("build/resources")
        archiveClassifier.set("")
    }
    register<Copy>("copyToServerMac") {   // 로컬 테스트 배포
        from(shadowJar)
        into("~/Desktop/MCServer/<server>/plugins")
    }
}
```

- `./gradlew :my-game-core:shadowJar` → 빌드
- `./gradlew :my-game-core:copyToServerMac` → 테스트 서버 복사
- `./gradlew :my-game-core:publish` → Nexus(`repo.s8u.kr`) 배포 (다른 모듈이 의존할 경우)

---

## 10. 체크리스트 & 자주 하는 실수

### ✅ 구현 체크리스트

- [ ] `Game` 상속 — 고유 `gameTypeId` 부여
- [ ] `PhasePipeline` 구성 — `StartCountPhase → ... → GamePhase → EndPhase` 순서
- [ ] `BukkitCorePlugin.onStart()`에서 `GameCore.init(this, game)` 호출
- [ ] `plugin.yml`에 `depend: [Core, GameCore]`
- [ ] game-core / core 의존성은 `compileOnly`
- [ ] (필요 시) Player 확장 후 `GameCore.unsafe.playerManager` 교체
- [ ] (필요 시) Config / Command / Listener 추가

### ❗ 자주 하는 실수

- **순서** — 다른 game-core API를 `GameCore.init()`보다 먼저 호출 → `lateinit` 예외
- **의존성** — game-core를 `implementation`으로 번들 → 클래스 충돌
- **스레드** — 코루틴 Phase에서 Bukkit API 직접 호출 → `BukkitSyncScope.launch` 누락
- **흐름** — `onRun`이 즉시 리턴 → Phase가 바로 끝나 다음 단계로 넘어감 (대기 루프 필요)
- **중복 ID** — 다른 미니게임과 `gameTypeId` 충돌

---

> 참고 구현체 전체 코드: `ability/ability-core` (`AbilityPlugin.kt`, `AbilityGame.kt`, `AbilityPhasePipeline.kt`, `game/`, `phases/`, `player/` 패키지)
