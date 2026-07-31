package frc.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
    private final SparkMax shooterMotor = new SparkMax(ShooterConstants.kShooterMotorCANID, MotorType.kBrushless);
    private final RelativeEncoder encoder = shooterMotor.getEncoder();
    private final SparkClosedLoopController closedLoopController = shooterMotor.getClosedLoopController();
    private double targetRPM = 0.0;

    public ShooterSubsystem() {
        shooterMotor.configure(Configs.ShooterConfig.shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void set(double speed) {
        targetRPM = 0.0;
        shooterMotor.set(speed);
    }

    public void setVelocity(double rpm) {
        targetRPM = rpm;
        closedLoopController.setSetpoint(rpm, ControlType.kVelocity);
    }

    public void runDefaultShot() {
        setVelocity(ShooterConstants.kShooterSpeedRPM);
    }

    public void stop() {
        targetRPM = 0.0;
        shooterMotor.stopMotor();
    }

    public double getVelocity() {
        return encoder.getVelocity();
    }

    public boolean atTarget() {
        return targetRPM > 0.0 && Math.abs(getVelocity() - targetRPM) <= ShooterConstants.kShooterToleranceRPM;
    }

    @Override
    public void periodic() {
        Logger.recordOutput("Shooter/Velocity RPM", getVelocity());
        Logger.recordOutput("Shooter/Target RPM", targetRPM);
        Logger.recordOutput("Shooter/At Target", atTarget());
        Logger.recordOutput("Shooter/Applied Output", shooterMotor.getAppliedOutput());
        Logger.recordOutput("Shooter/Current", shooterMotor.getOutputCurrent());
    }
}
