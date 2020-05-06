package application.activity;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.WavePoolLifeGuard;
import application.user.AdultUser;
import application.user.ChildUser;
import application.user.YoungUser;

public class WavePoolActivity extends BaseActivity {

    private CyclicBarrier enteringBarrier = new CyclicBarrier(ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_ENTRANCE_USERS);

    public WavePoolActivity(UserRegistry userRegistry) {
        super(ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_NAME, ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_CAPACITY, userRegistry);
    }

    @Override
    protected long getActivityTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MIN_MILISECONDS)
                + (ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MIN_MILISECONDS * Math.random()));
    }

    @Override
    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new WavePoolLifeGuard(ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    @Override
    public boolean goIn(ChildUser user) {
        boolean result = false;
        waitIfProgramIsStopped();

        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() == Permission.NOT_ALLOWED) {
                throw new SecurityException();
            } else if (user.getActivityPermissionType() == Permission.SUPERVISED) {
                passFromWaitingLineToActivity(user);
            } else if (user.getActivityPermissionType() == Permission.ALLOWED) {
                getEnteringBarrier().await();
                goOutWaitingLine(user);
                goIntoActivityAreaWithoutSupervisor(user);
            }

            result = true;
        } catch (SecurityException | InterruptedException | BrokenBarrierException e) {
            goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

    @Override
    public boolean goIn(AdultUser user) throws InterruptedException {
        boolean result = false;
        waitIfProgramIsStopped();

        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            getEnteringBarrier().await();
            result = passFromWaitingLineToActivity(user);

        } catch (SecurityException | BrokenBarrierException e) {
            goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

    @Override
    public boolean goIn(YoungUser user) throws InterruptedException {
        boolean result = false;
        waitIfProgramIsStopped();

        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            getEnteringBarrier().await();
            result = passFromWaitingLineToActivity(user);

        } catch (SecurityException | BrokenBarrierException e) {
            goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

    public CyclicBarrier getEnteringBarrier() {
        return enteringBarrier;
    }

}
