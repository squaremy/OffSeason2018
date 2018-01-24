package Actions;

import android.text.method.Touch;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.io.IOException;

import Actions.ActionHandler;
import MotorControllers.MotorController;

/**
 * Created by robotics on 1/22/18.
 */

public class NewSpoolMotor extends MotorController implements ActionHandler{
    private double extendSpeedInPerSecond = 0;
    private double retractSpeedInPerSecond = 0;
    private boolean shouldLimitRetraction = false;
    private TouchSensor spoolLimit;
    private long zeroPoint = 0;

    public NewSpoolMotor(DcMotor m, String configFileLoc, double extendInPerSecond, double retractInPerSecond, HardwareMap hw) throws IOException {
        super(m,configFileLoc,hw);
        extendSpeedInPerSecond = extendInPerSecond;
        retractSpeedInPerSecond = retractInPerSecond;
        zeroPoint = getCurrentTick();
    }

    public NewSpoolMotor(String motorName, String configFileLoc, double extendInPerSecond, double retractInPerSecond, HardwareMap hw) throws IOException {
        super(motorName,configFileLoc,hw);
        extendSpeedInPerSecond = extendInPerSecond;
        retractSpeedInPerSecond = retractInPerSecond;
        zeroPoint = getCurrentTick();
    }

    public NewSpoolMotor(String motorName, String configFileLoc, double extendInPerSecond, double retractInPerSecond, TouchSensor limit, HardwareMap hw) throws IOException {
        super(motorName,configFileLoc,hw);
        extendSpeedInPerSecond = extendInPerSecond;
        retractSpeedInPerSecond = retractInPerSecond;
        shouldLimitRetraction = true;
        spoolLimit = limit;
        zeroPoint = getCurrentTick();
    }

    public NewSpoolMotor(String motorName, String configFileLoc, String debugTag, double extendInPerSecond, double retractInPerSecond, HardwareMap hw) throws IOException {
        super(motorName,configFileLoc,debugTag,hw);
        extendSpeedInPerSecond = extendInPerSecond;
        retractSpeedInPerSecond = retractInPerSecond;
        zeroPoint = getCurrentTick();
    }

    public void extend(){
        if(getMotorRunMode() != DcMotor.RunMode.RUN_USING_ENCODER) setMotorRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setInchesPerSecondVelocity(extendSpeedInPerSecond);
    }

    public void pause(){
        setInchesPerSecondVelocity(0);
    }

    public void determineZeroPoint(){
        if (spoolLimit.isPressed()) {
            setMotorRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
            setMotorPower(.1);
            while (spoolLimit.isPressed());
            zeroPoint = getCurrentTick();
            holdPosition();
        } else {
            setMotorRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
            setMotorPower(-.1);
            while (!spoolLimit.isPressed()) ;
            brake();
            setMotorPower(.1);
            while (spoolLimit.isPressed()) ;
            zeroPoint = getCurrentTick();
            holdPosition();
        }
        //TODO set back down on the hard stops, no reason to keep it powered here.
    }

    public void retract(){
        if(getMotorRunMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) setMotorRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if(shouldLimitRetraction) {
            if (!spoolLimit.isPressed())
                setInchesPerSecondVelocity(-retractSpeedInPerSecond);
            else pause();
        }
        else setInchesPerSecondVelocity(-retractSpeedInPerSecond);
    }

    @Override
    public boolean doAction(String action, long maxTimeAllowed) {
        return false;
    }

    @Override
    public boolean stopAction(String action) {
        return false;
    }

    @Override
    public boolean startDoingAction(String action) {
        return false;
    }
}
