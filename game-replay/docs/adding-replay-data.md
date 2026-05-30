# game-replay 기반 리플레이 데이터 추가 가이드

> **대상 모듈:** `game-replay` · **플랫폼:** Paper 1.8.8 / Kotlin 1.8 / ProtocolLib
> **참고 구현체:** `ability/ability-replay` · **작성일:** 2026-05-30

## 목차

1. [개요 — game-replay란 무엇인가](#1-개요--game-replay란-무엇인가)
2. [아키텍처 (Record · RecordData · Replay)](#2-아키텍처)
3. [핵심 3종 세트](#3-핵심-3종-세트)
4. [빠른 시작 — 데이터 1종 추가하기](#4-빠른-시작--데이터-1종-추가)
5. [단계별 구현](#5-단계별-구현)
6. [레지스트리 등록 & 직렬화](#6-레지스트리-등록--직렬화-동작)
7. [플러그인 배선 (Plugin · Api)](#7-플러그인-배선)
8. [직렬화 규칙 (반드시 지킬 것)](#8-직렬화-규칙-반드시-지킬-것)
9. [빌드 · 배포](#9-빌드--배포)
10. [체크리스트 & 자주 하는 실수](#10-체크리스트--자주-하는-실수)

---

## 1. 개요 — game-replay란 무엇인가

`game-replay`는 game-core 게임을 **녹화(Record)** 했다가 나중에 **재생(Replay)** 하는 모듈이다. 게임 중 일어나는 모든 사건을 **틱(tick) 단위 `RecordData` 목록**으로 기록하고, 재생 시 그 데이터를 틱 순서대로 꺼내 가상 플레이어/엔티티/블록으로 화면에 복원한다.

game-replay는 기본적으로 블록·엔티티·플레이어·게임 상태에 대한 데이터 종류를 다수 내장하고 있다. 하지만 **각 미니게임만의 고유 사건**(예: 능력자 게임의 "능력 추첨", "능력 발동")은 game-replay가 알 수 없으므로, **해당 게임의 replay 모듈에서 새 데이터 종류를 추가**해야 한다.

이 가이드는 `ability-replay`(능력자 게임 리플레이 확장)를 표준 참고 구현체로 삼아, 새로운 리플레이 데이터를 추가하는 방법을 설명한다.

> ⚠️ **전제:** 리플레이 데이터를 추가하려는 사건은 보통 **대상 게임 플러그인이 발행하는 Bukkit 이벤트**나 packet에서 출발한다. ability-replay가 `AbilityAssignEvent`를 듣고 데이터를 기록하듯, 먼저 "무엇을 신호로 잡을 것인가"를 정해야 한다.

---

## 2. 아키텍처

```
[ 녹화 서버 (isRecordServer) ]
  게임 이벤트/패킷 발생
        │
        ▼
  RecordListener (Bukkit Listener / PacketListener)
    └─ isRecording() 체크 → RecordData 생성
        │
        ▼
  RecordManager.record.addRecordData(data)   // 현재 틱에 적재
        │
        ▼
  Replay 객체  =  { tick → [RecordData, RecordData, ...] }
        │  (게임 종료 시 JSON 직렬화 → 압축 → MySQL 저장)
        ▼
  ┌──────────────────────────────────────────┐
  ▼
[ 재생 서버 (isReplayPlayServer) ]
  DB에서 Replay 로드 → JSON 역직렬화
    └─ RecordDataDeserializer 가 type 으로 클래스 매핑
        │
        ▼
  틱 진행 → 해당 틱의 RecordData 마다
    RecordDataReplayHandler.onPlay(data, viewer) 호출   // 화면 복원
```

> 녹화와 재생은 `replay-config.yml`의 `isRecordServer` / `isReplayPlayServer` 플래그로 서버 역할이 갈린다. game-replay는 녹화 서버에서는 `registerRecordListeners()`를, 재생 서버에서는 `registerReplayHandlers()`를 호출한다.

---

## 3. 핵심 3종 세트

새 리플레이 데이터 1종은 **항상 3개의 클래스**로 구성된다. ability-replay의 디렉토리 구조가 이 셋을 그대로 분리해 둔다.

| | 클래스 | 역할 | 위치 |
|---|---|---|---|
| ① **Data** | `RecordData` 구현 `data class` | 기록할 값과 고유 `type` 문자열 | `data/datas/...` |
| ② **RecordListener** | `Listener` | 녹화 서버에서 이벤트/패킷을 듣고 Data를 만들어 `addRecordData()` | `data/records/...` |
| ③ **ReplayHandler** | `RecordDataReplayHandler` | 재생 서버에서 Data를 받아 화면에 복원 | `data/replays/...` |

| game-replay 인터페이스 | 역할 | 위치 |
|---|---|---|
| `RecordData` | `val type: String` 하나만 요구하는 마커 인터페이스 | `data/RecordData.kt` |
| `RecordDataReplayHandler<T>` | `onPlay(data, viewer)` 하나를 요구 | `replay/RecordDataReplayHandler.kt` |
| `RecordDataManager` | `registerRecordData(class, handler)`로 둘을 연결 + type↔class 매핑 | `replay/RecordDataManager.kt` |

---

## 4. 빠른 시작 — 데이터 1종 추가

"능력 할당(AbilityAssign)" 데이터의 실제 구현 전체다. 이 셋을 그대로 템플릿으로 복사해 쓰면 된다.

### ① Data
> `data/datas/ability/AbilityAssignData.kt`

```kotlin
data class AbilityAssignData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null
) : RecordData {
    override val type: String = "abilityAssign"   // 전역 유일!
}
```

### ② RecordListener (녹화)
> `data/records/ability/AbilityAssignDataRecordListener.kt`

```kotlin
class AbilityAssignDataRecordListener : Listener {
    @EventHandler
    fun onAbilityAssign(event: AbilityAssignEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityAssignData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }
}
```

### ③ ReplayHandler (재생)
> `data/replays/ability/AbilityAssignDataReplayHandler.kt`

```kotlin
class AbilityAssignDataReplayHandler : RecordDataReplayHandler<AbilityAssignData> {
    override fun onPlay(recordData: AbilityAssignData, viewer: Player) {
        val playerName = CommonSenderModule.getOfflinePlayer(recordData.playerUuid)?.displayName
            ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력: §f${recordData.abilityName}")
    }
}
```

### 레지스트리 등록
> `replay/AbilityReplayHandlerRegistry.kt`

```kotlin
GameReplayApi.unsafe.recordDataManager.registerRecordData(
    AbilityAssignData::class.java,
    AbilityAssignDataReplayHandler()
)
```

> ✅ 이 4곳만 추가하면 끝이다. 녹화 서버에서는 `AbilityAssignEvent`가 발생할 때마다 데이터가 틱에 쌓이고, 재생 서버에서는 그 틱에 도달할 때 `onPlay`가 호출되어 관전자(viewer)에게 복원된다.

---

## 5. 단계별 구현

### 1단계 — 기록할 사건과 신호원 정하기

먼저 "어떤 사건을, 무엇을 통해 감지할지" 결정한다.

- **Bukkit 이벤트** — 대상 게임이 발행하는 이벤트를 구독 (ability-replay 방식의 대부분)
- **패킷** — 클라이언트로 나가는 패킷을 ProtocolLib `PacketAdapter`로 가로채기 (game-replay의 엔티티/블록 데이터 방식)

> 커스텀 사건이라면 **대상 게임 플러그인이 그 이벤트를 발행하고 있어야** 한다. ability-core가 `AbilityAssignEvent`·`AbilityCooldownStartEvent` 등을 `HandlerEvent`로 발행하기 때문에 ability-replay가 그걸 들을 수 있다. 없다면 대상 게임 쪽에 이벤트를 먼저 추가한다.

### 2단계 — Data 클래스 작성
> `data/datas/<그룹>/XxxData.kt`

```kotlin
data class MyEventData(
    var playerUuid: UUID? = null,
    var value: Int = 0
) : RecordData {
    override val type: String = "myEvent"   // 전역 유일 문자열
}
```

- **반드시 `data class`** — `addRecordData()`가 `equals()`로 중복을 거른다.
- **모든 필드는 `var` + 기본값** — 역직렬화 시 `newInstance()`(no-arg) 후 필드를 채운다.
- **`type`은 전역 유일** — game-replay 내장 type 및 다른 확장과 겹치면 안 된다.

### 3단계 — RecordListener 작성 (녹화)
> `data/records/<그룹>/XxxDataRecordListener.kt`

```kotlin
class MyEventDataRecordListener : Listener {
    @EventHandler
    fun onMyEvent(event: MyEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return   // 가드 필수

        val data = MyEventData().apply {
            playerUuid = event.player.uniqueId
            value = event.value
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(data)
    }
}
```

> ⚠️ **`isRecording()` 가드를 절대 빼지 말 것.** 녹화 중이 아닐 때 `record!!`는 `null`이라 NPE가 난다. 모든 RecordListener의 첫 줄은 이 가드다.

### 4단계 — ReplayHandler 작성 (재생)
> `data/replays/<그룹>/XxxDataReplayHandler.kt`

```kotlin
class MyEventDataReplayHandler : RecordDataReplayHandler<MyEventData> {
    override fun onPlay(recordData: MyEventData, viewer: Player) {
        // viewer = 리플레이를 보고 있는 관전자
        // 메시지, 홀로그램, 파티클, 가상 엔티티 갱신 등으로 사건을 복원
        viewer.sendMessage("§e${recordData.value}")
    }
}
```

> `onPlay`는 **그 틱에 도달한 각 viewer마다** 호출된다. 따라서 여기서 하는 작업은 "이 관전자에게 어떻게 보여줄 것인가"여야 한다. 월드 전역 상태 변경이 아니라 viewer 기준 표현으로 작성한다.

---

## 6. 레지스트리 등록 & 직렬화 동작

작성한 Data ↔ Handler를 `RecordDataManager`에 등록해야 비로소 동작한다.

```kotlin
GameReplayApi.unsafe.recordDataManager.registerRecordData(
    MyEventData::class.java,
    MyEventDataReplayHandler()
)
```

`registerRecordData()`는 내부에서 두 가지를 동시에 수행한다.

```kotlin
fun registerRecordData(recordDataClass, handler) {
    set(recordDataClass, handler)                            // ① type별 재생 핸들러 매핑
    recordDatas.put(recordDataClass.newInstance().type,      // ② type 문자열 → 클래스 매핑
                    recordDataClass)
}
```

> ⚠️ ②번이 **역직렬화의 핵심**이다. 저장된 JSON에는 `"type": "myEvent"`만 들어있고, `RecordDataDeserializer`가 이 `type`으로 클래스를 찾아 `newInstance()` 후 필드를 채운다. 그래서 **등록을 빠뜨리면 "Cannot deserialize RecordData type ..." 에러**로 리플레이 로드가 실패한다.

### 역직렬화 흐름

```kotlin
// RecordDataDeserializer
val type = jsonObject.get("type").asString
val clazz = recordDataManager.getRecordDataClassByType(type)
    ?: error("Cannot deserialize RecordData type ${type}")
val recordData = clazz.newInstance()              // ← no-arg 생성자 필요
GsonUtil.loadFromJson(jsonObject.toString(), recordData)
```

---

## 7. 플러그인 배선

새 데이터들을 한 곳에 모아 등록하는 Registry 클래스를 만들고, 플러그인 진입점에서 호출한다. ability-replay의 구조를 그대로 따른다.

### Registry — 모든 등록을 한곳에
> `replay/MyReplayHandlerRegistry.kt`

```kotlin
class MyReplayHandlerRegistry {
    fun registerHandlers() {
        GameReplayApi.unsafe.recordDataManager.registerRecordData(
            MyEventData::class.java, MyEventDataReplayHandler())
        // ... 추가 데이터들
    }
}
```

### Api 객체 — 초기화 집약
> `MyReplayApi.kt`

```kotlin
object MyReplayApi {
    object unsafe {
        lateinit var handlerRegistry: MyReplayHandlerRegistry
        fun init() {
            handlerRegistry = MyReplayHandlerRegistry()
            handlerRegistry.registerHandlers()
        }
    }
    fun init() { unsafe.init() }
}
```

### Plugin 진입점
> `MyReplayPlugin.kt`

```kotlin
class MyReplayPlugin : BukkitCorePlugin() {
    companion object { lateinit var instance: MyReplayPlugin }

    override fun onStart() {
        instance = this
        prefix = "§c[ MyReplay ]"
        registerLoggerPrefix()

        MyReplayApi.init()                       // ReplayHandler 등록

        // 녹화 서버일 때만 RecordListener 등록
        if (Bukkit.getPluginManager().getPlugin("GameCore") != null) {
            registerEventListeners()             // data/records/** 의 Listener 자동 등록
        }
    }
}
```

> **왜 ReplayHandler는 항상 등록하고 RecordListener는 조건부인가?** 재생 서버에는 GameCore 게임이 없을 수 있으므로 녹화용 이벤트 리스너는 game-core 존재 시에만 켠다. 반면 type↔class 매핑(ReplayHandler 등록)은 **역직렬화를 위해 양쪽 서버 모두에 필요**하다.

### plugin.yml

```yaml
name: $pluginName
version: $version
main: $main
depend:
  - Core
  - GameReplay        # game-replay 필수
softdepend:
  - MyGame            # 대상 게임 플러그인 (이벤트 소스). soft 로 두어 재생 전용 서버 호환
```

---

## 8. 직렬화 규칙 (반드시 지킬 것)

- **`data class` 사용** — 틱 내 중복 데이터 제거(`contains`)가 `equals`에 의존
- **no-arg 생성 가능해야 함** — 모든 필드 `var` + 기본값. `newInstance()`로 빈 객체를 만든 뒤 채운다
- **`type` 전역 유일 + 고정** — 한 번 저장된 리플레이는 같은 `type`으로만 다시 읽을 수 있다. 출시 후 변경 금지
- **Gson 직렬화 가능한 타입만** — UUID·String·숫자·enum 권장. Bukkit `Location`·`ItemStack`은 그대로 넣지 말고 원시 필드(`worldName`, `x/y/z`, `yaw/pitch` 등)로 분해 (game-replay 내장 Data들의 관례)
- **등록 누락 금지** — Registry에 빠지면 역직렬화 단계에서 전체 리플레이 로드가 실패

### 데이터 그룹 컨벤션

ability-replay는 사건 성격별로 하위 패키지를 나눈다. 새 모듈도 동일하게 정리한다.

```
data/
├── datas/
│   ├── ability/     AbilityAssignData, AbilityCooldownStartData ...
│   └── game/        AbilityDrawCompleteData ...
├── records/         (datas 와 동일한 그룹 구조)
│   ├── ability/
│   └── game/
└── replays/         (datas 와 동일한 그룹 구조)
    ├── ability/
    └── game/
```

---

## 9. 빌드 · 배포

리플레이 확장 모듈은 대상 게임 코어를 `compileOnly(project(...))`로 참조하는 경우가 많다 (이벤트 클래스 접근 위함). ability-replay의 설정 그대로다.

```kotlin
// my-replay/build.gradle.kts
plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
}
dependencies {
    compileOnly(project(":my-game-core"))      // 이벤트 소스 참조
    // game-replay / core 는 루트 allprojects 에서 compileOnly 로 제공
}
tasks {
    withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }
    processResources { filesMatching("**/*.yml") { expand(project.properties) } }
    withType<ShadowJar> { delete("build/resources"); archiveClassifier.set("") }
}
```

- `./gradlew :my-replay:shadowJar` → 빌드
- 녹화 서버 / 재생 서버 양쪽 plugins 폴더에 동일 jar 배치

---

## 10. 체크리스트 & 자주 하는 실수

### ✅ 구현 체크리스트 (데이터 1종당)

- [ ] `XxxData : RecordData` — `data class`, `var`+기본값, 유일 `type`
- [ ] `XxxDataRecordListener : Listener` — `isRecording()` 가드 → `addRecordData()`
- [ ] `XxxDataReplayHandler : RecordDataReplayHandler<XxxData>` — `onPlay(data, viewer)`
- [ ] Registry에 `registerRecordData(XxxData::class.java, XxxDataReplayHandler())` 추가
- [ ] RecordListener는 플러그인 패키지에 위치 → `registerEventListeners()`가 자동 등록

### ❗ 자주 하는 실수

- **등록 누락** — Registry에 안 넣음 → "Cannot deserialize RecordData type ..." 로 리플레이 로드 실패
- **NPE** — RecordListener에서 `isRecording()` 가드 누락 → `record!!` NPE
- **중복 type** — 다른 데이터와 `type` 문자열 충돌 → 역직렬화가 엉뚱한 클래스로 매핑
- **non-data class** — 일반 class로 만들어 틱 내 중복 제거가 안 됨 / equals 미동작
- **type 변경** — 출시 후 `type` 변경 → 기존 저장 리플레이 로드 불가
- **복합 타입** — `Location`·`ItemStack`을 필드로 직접 저장 → Gson 직렬화/역직렬화 문제

---

> 참고 구현체 전체 코드: `ability/ability-replay` (`AbilityReplayPlugin.kt`, `AbilityReplayApi.kt`, `replay/AbilityReplayHandlerRegistry.kt`, `data/datas|records|replays/**`) · game-replay 본체: `game-core/game-replay`
