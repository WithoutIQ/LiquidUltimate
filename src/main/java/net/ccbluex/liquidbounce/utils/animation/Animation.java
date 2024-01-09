package net.ccbluex.liquidbounce.utils.animation;

public class Animation {

    private long duration = 0;
    private long startTime = 0;
    private double start = 0.0;
    public double value = 0.0;
    public double end = 0.0;
    private Type type = Type.LINEAR;
    private boolean started = false;

    public void start(double start, double end, float duration, Type type) {
        if (!started) {
            if (start != this.start || end != this.end || (long) (duration * 1000) != this.duration || type != this.type) {
                this.duration = (long) (duration * 1000);
                this.start = start;
                this.startTime = System.currentTimeMillis();
                value = start;
                this.end = end;
                this.type = type;
                started = true;
            }
        }
    }

    public void update() {
        if (!started) return;

        double result;
        switch (type) {
            case LINEAR:
                result = AnimationUtils.linear(startTime, duration, start, end);
                break;
            case EASE_IN_QUAD:
                result = AnimationUtils.easeInQuad(startTime, duration, start, end);
                break;
            case EASE_OUT_QUAD:
                result = AnimationUtils.easeOutQuad(startTime, duration, start, end);
                break;
            case EASE_IN_OUT_QUAD:
                result = AnimationUtils.easeInOutQuad(startTime, duration, start, end);
                break;
            case EASE_IN_ELASTIC:
                result = AnimationUtils.easeInElastic(System.currentTimeMillis() - startTime, start, end - start, duration);
                break;
            case EASE_OUT_ELASTIC:
                result = AnimationUtils.easeOutElastic(System.currentTimeMillis() - startTime, start, end - start, duration);
                break;
            case EASE_IN_OUT_ELASTIC:
                result = AnimationUtils.easeInOutElastic(System.currentTimeMillis() - startTime, start, end - start, duration);
                break;
            case EASE_IN_BACK:
                result = AnimationUtils.easeInBack(System.currentTimeMillis() - startTime, start, end - start, duration);
                break;
            case EASE_OUT_BACK:
                result = AnimationUtils.easeOutBack(System.currentTimeMillis() - startTime, start, end - start, duration);
                break;
            default:
                result = value; // Default to the current value if type is unknown
                break;
        }

        value = result;

        if ((System.currentTimeMillis() - startTime) > duration) {
            started = false;
            value = end;
        }
    }

    public void reset() {
        value = 0.0;
        start = 0.0;
        end = 0.0;
        startTime = System.currentTimeMillis();
        started = false;
    }

    public void fstart(double start, double end, float duration, Type type) {
        started = false;
        start(start, end, duration, type);
    }

    public double getValue() {
        return value;
    }

    public boolean isStarted() {
        return started;
    }
}
