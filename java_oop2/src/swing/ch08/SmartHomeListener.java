package swing.ch08;

public interface SmartHomeListener {
    public abstract void onPowerOn();
    public abstract void onPowerOff();
    public abstract void onTemperatureChange(int t);
}
