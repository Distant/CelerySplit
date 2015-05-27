package philoats.celerysplit.enums;

public enum TimerEvent {
    STARTED, // timer started
    RESET, // timer reset (ready to start)
    PAUSED, //timer paused
    UNPAUSED, //timer unpaused
    FINISHED, //timer ready to be reset
    UNLOADED, // timer not ready to start
    READY, // timer ready to start
}