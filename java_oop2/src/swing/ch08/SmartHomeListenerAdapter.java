package swing.ch08;

public abstract class SmartHomeListenerAdapter implements  SmartHomeListener {
    @Override
    public void onPowerOn() {}

    @Override
    public void onPowerOff() {}

    @Override
    public void onTemperatureChange(int t) {}
}
