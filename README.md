# GameCore

Minecraft 미니게임 서버를 위한 핵심 프레임워크 플러그인입니다. Paper 1.8.8 기반의 미니게임 개발에 필요한 공통 기능을 제공합니다.

## 프로젝트 구조

| 모듈 | 설명 |
|------|------|
| `game-core` | 게임 라이프사이클, 플레이어, 팀, 맵, 페이즈 등 핵심 기능 |
| `game-history` | 게임 전적 기록 및 관리 |
| `game-replay` | 게임 리플레이 녹화 및 재생 (GZIP 압축 지원) |
| `game-reconnector` | 게임 중 재접속 처리 |
| `game-profile` | 플레이어 프로필 관리 |

## 주요 기능

- **게임 관리** - 게임 초기화, 시작, 종료, 취소 등 전체 라이프사이클 관리
- **페이즈 시스템** - 카운트다운, 무적, 월드보더 축소 등 게임 단계별 파이프라인
- **팀 시스템** - 팀 생성, 팀별 PvP 제어, 네임태그 관리
- **맵 시스템** - SlimeWorldManager 기반 맵 로드/언로드, 맵 투표
- **킷 시스템** - 킷 구성 및 편집 GUI
- **보급 시스템** - 인게임 보급 드롭
- **투표 시스템** - 게임 시작, 맵 선택, 무적 스킵, 노인챈트 투표
- **시작 아이템** - 게임 시작 시 지급 아이템 설정
- **사이드바** - 게임 정보 표시용 스코어보드
- **관전 모드** - 관전자 제어, 텔레포터 GUI, 퀵바
- **핵 방지 우회** - NoCheatPlus 연동, 게임 중 필요한 체크 우회
- **자동 재부팅** - 게임 종료 후 서버 자동 재부팅 예약
- **킬스트릭** - 연속 킬 추적

## 기술 스택

- **언어**: Kotlin 1.8.10
- **플랫폼**: Paper API 1.8.8
- **빌드**: Gradle (Kotlin DSL), Shadow Plugin
- **JVM**: Java 8

## 의존성

- [Pooleaf Core](https://repo.s8u.kr) - 공통 모듈 프레임워크
- [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) 4.8.0 - 패킷 조작
- [SlimeWorldManager](https://github.com/Paul19988/Advanced-Slime-World-Manager) 2.2.1 - 월드 관리
- [Citizens](https://github.com/CitizensDev/Citizens2) 2.0.30 - NPC
- [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) 1.3.1 - 머리 데이터베이스
- [NoCheatPlus](https://github.com/Updated-NoCheatPlus/NoCheatPlus) 3.16.0 - 핵 방지

## 빌드

```bash
./gradlew shadowJar
```

## 사용법

GameCore를 의존성으로 추가한 뒤 `Game` 클래스를 구현하여 미니게임을 개발합니다.

```kotlin
class MyGame : Game() {
    override val gameTypeId = "my-game"

    override fun init() {
        // 게임 초기화
    }
}
```

플러그인에서 GameCore를 초기화합니다.

```kotlin
GameCore.init(this, MyGame())
```

## 설정 파일

| 파일 | 설명 |
|------|------|
| `game-config.yml` | 게임 기본 설정 |
| `spawn-config.yml` | 스폰 위치 설정 |
| `quickbar-config.yml` | 퀵바 아이템 설정 |
| `team-config.yml` | 팀 설정 |
| `auto-reboot-config.yml` | 자동 재부팅 설정 |

## 라이선스

이 프로젝트는 Pooleaf 내부 프로젝트입니다.
