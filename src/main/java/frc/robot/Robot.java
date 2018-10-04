package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Drivetrain.Drivetrain;
import frc.robot.Intake.Intake;
import frc.robot.OI.OI;

public class Robot extends TimedRobot {

  public static OI oi;
  public static Drivetrain drivetrain;
  public static Intake intake;

  @Override
  public void robotInit() {
    drivetrain = Drivetrain.getInstance();
    //intake = Intake.getInstance();
    oi = new OI();

  }

  @Override
  public void disabledInit() {
    Drivetrain.resetEncoders();
    Drivetrain.resetGyro();
    Scheduler.getInstance().removeAll();
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Left Motor Output", Drivetrain.leftMotorA.getMotorOutputPercent());
    SmartDashboard.putNumber("Right Motor Output", Drivetrain.rightMotorA.getMotorOutputPercent());

    SmartDashboard.putNumber("Raw Gyro Heading", Drivetrain.gyro.getAngle());
    SmartDashboard.putNumber("Right Encoder", Drivetrain.rightMotorA.getSelectedSensorPosition(0));
    SmartDashboard.putNumber("Left Encoder", Drivetrain.leftMotorA.getSelectedSensorPosition(0));

  }

  @Override
  public void autonomousInit() {
      new PathFollower("Straight15ft").start();
  }

  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    Drivetrain.stopCompressor();
  }

  public void teleopInit(){
    Drivetrain.setBrakeMode();

  }

  @Override
  public void testPeriodic() {
    Scheduler.getInstance().run();
  }
}
