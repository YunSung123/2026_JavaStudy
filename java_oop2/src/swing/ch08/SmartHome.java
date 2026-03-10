package swing.ch08;

public class SmartHome{
    public static void main(String[] args) {

        // 1. 인터페이스를 직접 구현할 때 (지저분함)
        // 익명 클래스 활용
        SmartHomeListener homeListener1 = new SmartHomeListener() {
            @Override
            public void onPowerOn() {

            }

            @Override
            public void onPowerOff() {

            }

            @Override
            public void onTemperatureChange(int t) {

            }
        };

        // 어댑터 추상 클래스를 사용할 떄 깔끔함
        SmartHomeListenerAdapter listenerAdapter = new SmartHomeListenerAdapter() {
            @Override
            public void onTemperatureChange(int t) {
                System.out.println("에어컨 가동 : 현재 온도 : " + t + "도 최적화중 ...");
            }
        };

        // 사용해 보기
        listenerAdapter.onTemperatureChange(23);

    } // end of main
}
