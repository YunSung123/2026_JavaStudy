package innerclass;

// 익명 내부클래스 사용해 보기
public class OuterClass4 {

    Runnable runnable; // 인터페이스

    public OuterClass4(Runnable runnable) {
        // 인터페이스를 구현 익명 클래스로 생성시킬 수 있다
        // 배내부에서 추상 메서드를 일반 메서드로 재 정의해서 마치 실제 클래스처럼 구현해서 사용할 수 있다.
        new Runnable() {
            @Override
            public void run() {

            }
        };
    } // end of 생성자

    // 메버 변수 선언과 동시에 인터페이스를 구현 클래스로 선언과 동시에 초기화
    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {

        }
    };
}
