package application.user;

import application.AquaticPark;
import application.UserRegistry;
import application.config.ApplicationGlobalConfig;

public class UserGenerator extends Thread {

    private AquaticPark park;
    private UserRegistry userRegistry;

    public UserGenerator(AquaticPark park) {
        this.park = park;
        this.userRegistry = park.getRegistry();
    }

    private boolean needsSupervisor(int age) {
        return age < ApplicationGlobalConfig.USER_WITH_SUPERVISOR_LIMIT_AGE;
    }

    private boolean isNotAdult(int age) {
        return age >= 0 && age < ApplicationGlobalConfig.USER_ADULT_AGE;
    }
    
    private int randomAge() {
    	return (int) ((ApplicationGlobalConfig.USER_SUPERVISOR_LIMIT_AGE) * Math.random() + 1);
    }
    
    private int randomSupervisorAge() {
    	return (int) (
        		(ApplicationGlobalConfig.USER_SUPERVISOR_LIMIT_AGE - ApplicationGlobalConfig.USER_ADULT_AGE) * Math.random()
        		+ ApplicationGlobalConfig.USER_ADULT_AGE);
    }
    
    private String generateSimpleId(int count, int age) {
    	return  "ID" + count + "-" + age;
    }
    
    private String generateChildId(int count, int age) {
    	return  "ID" + count + "-" + age + "-" + (count + 1);
    }
    
    private String generateSupervisorId(int count, int age, int childId) {
    	return  "ID" + count + "-" + age + "-" + childId;
    }
    
    public SupervisorUser createSupervisorUser(int childId, int count) {
        int age = randomSupervisorAge();
        String identificator = generateSupervisorId(count, age, childId);
        return new SupervisorUser(identificator, age, park);
    }

    public AdultUser createAdultUser(int count, int age) {
        String identificator = generateSimpleId(count, age);
        return new AdultUser(identificator, age, park);
    }

    public YoungUser crearYoungUser(int count, int age) {
        String identificator = generateSimpleId(count, age);
        return new YoungUser(identificator, age, park);
    }

    public ChildUser createChildUser(int count, int age) {
        String identificator = generateChildId(count, age);
        SupervisorUser supervisor = createSupervisorUser(count, count + 1);
        return new ChildUser(identificator, age, supervisor, park);
    }
    
    private void sleepStepGeneration() throws InterruptedException {
    	sleep((long) (
    			(ApplicationGlobalConfig.USER_GENERATION_MAX_MILISECONDS - ApplicationGlobalConfig.USER_GENERATION_MIN_MILISECONDS) + 
    			(ApplicationGlobalConfig.USER_GENERATION_MIN_MILISECONDS * Math.random())));
    }

    @Override
    public void run() {
        int count = 1;

        try {
            while (count < ApplicationGlobalConfig.TOTAL_CREATED_USERS) {
            	String identificator;
                User visitor;
                SupervisorUser supervisor = null;
                
                userRegistry.waitIfProgramIsStopped();
                sleepStepGeneration();

                int randomAge = randomAge();
                
                if (isNotAdult(randomAge)) {
                    if (needsSupervisor(randomAge)) {
                        identificator = generateChildId(count, randomAge);
                        supervisor = createSupervisorUser(count, count + 1);
                        count++;
                        visitor = new ChildUser(identificator, randomAge, supervisor, park);
                    } else {
                        identificator = generateSimpleId(count, randomAge);
                        visitor = new YoungUser(identificator, randomAge, park);
                    }

                } else {
                    identificator = generateSimpleId(count, randomAge);
                    visitor = new AdultUser(identificator, randomAge, park);
                }
                
                userRegistry.addUser(visitor);
                visitor.start();
                
                if (supervisor != null) {
                    userRegistry.addUser(supervisor);
                    supervisor.start();
                }
                
                count++;
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
