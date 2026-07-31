package frc.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.ClimberConstants;

public class Climber extends SubsystemBase {
    // TODO: Make it so that the climber doesn't go too high or too low using the encoder, remember it's in rotations =3
    // Example solution at the bottom of the file
    private final SparkMax climberMotor = new SparkMax(ClimberConstants.kClimberMotorCANID, MotorType.kBrushless);
    private final AbsoluteEncoder climberEncoder = climberMotor.getAbsoluteEncoder();
    private double targetSpeed = 0.0;

    public Climber() {
        climberMotor.configure(Configs.ClimberConfig.climberConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    @Override
    public void periodic() {
        Logger.recordOutput("Climber/Position", getClimberPosition());
        Logger.recordOutput("Climber/Target Speed", targetSpeed);
        Logger.recordOutput("Climber/Applied Output", climberMotor.getAppliedOutput());
        Logger.recordOutput("Climber/Current", climberMotor.getOutputCurrent());
    }

    public void setClimberSpeed(double speed) {
        targetSpeed = speed;
        climberMotor.set(speed);
    }

    public void climbUp() {
        setClimberSpeed(ClimberConstants.kClimberSpeed);
    }

    public void climbDown() {
        setClimberSpeed(-ClimberConstants.kClimberSpeed);
    }

    public void stop() {
        setClimberSpeed(0.0);
    }

    public double getClimberPosition() {
        return climberEncoder.getPosition();
    }

    public boolean climberTooLow() {
        double position = getClimberPosition();
        return position < ClimberConstants.kClimberDownPosition;
    }

    public boolean climberTooHigh() {
        double position = getClimberPosition();
        return position > ClimberConstants.kClimberUpPosition;
    }
}


// Add to the bottom of periodic:
// if (climberTooHigh() && targetSpeed > 0) {
//     stop();
// } else if (climberTooLow() && targetSpeed < 0) {
//     stop();
// }
