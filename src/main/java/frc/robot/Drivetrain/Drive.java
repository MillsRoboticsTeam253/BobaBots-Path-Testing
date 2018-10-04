package frc.robot.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class Drive extends Command {
    private static double right, left;

    private static final double kJoystickDeadband = 0.05;

    /**
     * Left: 21.51 ft/s, 6.559 m/s, equation: 582x-796, intercept at 1.3677v
     * Right 20.51 ft/s, 6.253 m/s, equation: 553x-736, intercept at 1.3309v
     * Average: 21.02 ft/s, 6.406 m/s
     */
    public static final double kLinterceptHigh = 0.0;//1.3677; 
    public static final double kRinterceptHigh = 0.0;//1.3309;

    /**
     * Left: 10.22 ft/s, 3.11 m/s, equation: 265x-242, intercept at 0.913v
     * Right: 9.67 ft/s, 2.94 m/s, equation: 251x-231, intercept at 0.920v
     * Average: 9.94ft/s, 3.025 m/s
     */
    public static final double kLinterceptLow = 0.968;
    public static final double kRinterceptLow = 1.058;

    public Drive(){
        requires(Drivetrain.getInstance());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    protected void execute(){
        
        //Getting the raw joystick values from OI
        double throttle = Robot.oi.throttleValue();
        double turn = Robot.oi.turnValue();
        
        //Deadbanding the joystick values to avoid moving when there is no input
        throttle = deadbandX(throttle, kJoystickDeadband);
        turn = deadbandX(turn, kJoystickDeadband);
        
        
        if(throttle == 0){ //Quickturning when no input from throttle stick
            left = turn;
            right = -turn;
        } else { //Binary curvature drive when throttle stick has input, squares outputs to add sensitivity curve
            left = throttle+throttle*turn;
            right = throttle-throttle*turn;

            left = exponentiate(left, 2);
            right = exponentiate(right, 2);
        }
        
        //Checks the current position of the shifters in order to determine which values to deadband the motor output to
        switch(Drivetrain.shifter.get()){
            case kForward: 
                left = deadbandY(left, kLinterceptHigh/12.0);
                right = deadbandY(right, kRinterceptHigh/12.0);
                break;
            case kReverse:
                left = deadbandY(left, kLinterceptLow/12.0);
                right = deadbandY(right, kRinterceptLow/12.0);
            case kOff:
                break;
        }
        
        //Drives the motors at calculated speeds
        Drivetrain.drive(left, right);
    }

    /**
     * Returns 0 if absolute value of input is less than deadband
     * Rescales values of input using the line going from (+-deadband, 0) and (+-1, +-1)
     * Can be used to achieve the full range of values while still deadbanding a joystick
     * 
     * @param input      an input value to test for deadband
     * @param deadband   deadband value to check the input against
     * @return           returns a double rescaled to fit a deadband
     */
    public static double deadbandX(double input, double deadband){
        if(Math.abs(input) <= deadband){
            return 0;
        } else if(Math.abs(input)==1) {
            return input;
        } else {
            return (1/(1-deadband)*(input+Math.signum(-input)*deadband));
        }
    }

    /**
     * Used to achieve a higher degree of sensitivity when driving
     * Smaller inputs are smaller, larger inputs are larger, without exceeding maximum
     * 
     * @param input   number to put to a power
     * @param power   number to exponentiate input by (not necessarily an integer)
     * @return        returns a double, squared, with the same sign as input
     *     
     */
    public static double exponentiate(double input, double power){
        return Math.copySign(Math.pow(input, power),input);
    }

    /**
     * Shifts the input upwards, similar to a vertical deadband
     * Can be used in unison with deadbandX to create a deadband zone in the horiz. and vert. directions
     * i.e. Rescaling the output of a joystick from 50-100 while also having an input deadband
     * 
     * @param input      input value to test for deadband 
     * @param deadband   determines values where method returns 0
     * @return           returns a double rescaled according to deadband
     */
    public static double deadbandY(double input, double deadband){
        if(Math.abs(input)==0.0){
            return 0;
        } else if(Math.abs(input) ==1){
            return input;
        } else {
            return input*(1.0-deadband)+Math.signum(input)*deadband;
        }
    }


}
