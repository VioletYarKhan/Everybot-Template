package frc.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.IntakeConstants;

public class Indexer extends SubsystemBase {
    // TODO: Try this as Duty Cycle, Velocity PID, and maybe even a torque bang-bang to see what works best
    private final SparkMax indexerMotor = new SparkMax(IntakeConstants.kIntakeMotorCANID, MotorType.kBrushless);
    private double targetSpeed = 0.0;

    public Indexer() {
        indexerMotor.configure(Configs.IndexerConfig.indexerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void set(double speed) {
        targetSpeed = speed;
        indexerMotor.set(speed);
    }

    public void intake() {
        set(IntakeConstants.kIntakeSpeed);
    }

    public void outtake() {
        set(IntakeConstants.kOuttakeSpeed);
    }

    public void feedShooter() {
        set(IntakeConstants.kFeedShooterSpeed);
    }

    public void stop() {
        set(0.0);
    }

    @Override
    public void periodic() {
        Logger.recordOutput("Indexer/Target Speed", targetSpeed);
        Logger.recordOutput("Indexer/Applied Output", indexerMotor.getAppliedOutput());
        Logger.recordOutput("Indexer/Current", indexerMotor.getOutputCurrent());
    }
}
