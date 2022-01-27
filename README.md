# RemindApp
Remind Application Project for Delight Room

### 메인화면
요구사항에 맞게, 추가버튼 클릭시 추가를 위한 리마인드 설정화면 이동
목록의 아이템 클릭 시, 수정을 위한 리마인드 설정화면 이동
목록의 아이템 활성화 버튼으로 리마인드 활성화 및 비활성화 가능

활성화시 pending intent를 등록하고 가장 빨리 실행되어야할 알람이 등록되어있다.
이후 앱 알람 작동 시 다시 다음 알람으로 교체한다.

### 설정 화면
추가 시, 이름/시간/벨소리 선택되어 있지 않음
수정 시, 저장하고 있던 이름/시간/벨소리가 선택되어 있음

저장 버튼을 눌러 리마인드를 추가하거나 수정할 수 있다

### 리마인드 알림
해당 시간이 되면 알람이 울린다.
리마인드 정보가 화면에 보여진다 (시간, 제목)

**pendingIntent로 receiver를 실행하고 receiver를 통해 service 실행한다. service 내부에서 activity를 실행하여 알림화면을 띄우는 로직이다.**

꺼져있던 화면이 켜진다

Dismiss 버튼을 누르면, 해당 리마인드의 아이템이 비활성화 된다.

재부팅 시 재실행은 구현하지 못하였다.