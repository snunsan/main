package seedu.taskman.logic.commands;

import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Status;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.UniqueActivityList;

/**
 * Completes an existing task
 * 
 * Note: Deeply resembles DeleteCommand (in order to get the Activity reference from the index)
 * 		 and EditCommand (make a new Activity with the correct status and then deleting the old one)
 */
public class CompleteCommand extends Command {
	
	public static final String COMMAND_WORD = "complete";
	private static final String STATUS_COMPLETE = "complete";
	
	public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks an existing task as complete. "
            + "Parameters: INDEX\n"
            + "Example: " + COMMAND_WORD
            + " 1";

    public static final String MESSAGE_SUCCESS = "Task completed: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "A task with the same name already exists in TaskMan";
    
    private Activity.ActivityType activityType;
    private Activity afterComplete;
    private int targetIndex;

    public CompleteCommand(int targetIndex) {
    	this.targetIndex = targetIndex;
	}

	@Override
	public CommandResult execute() {
		assert model != null;
		
		UnmodifiableObservableList<Activity> lastShownList = model.getFilteredActivityList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Activity activityToComplete = lastShownList.get(targetIndex - 1);
        activityType = activityToComplete.getType();
        
        switch (activityType){
	        case TASK:
	        default: {
	        	Task task = new Task(
	                    activityToComplete.getTitle(),
	                    activityToComplete.getTags(),
	                    activityToComplete.getDeadline().get(),
	                    activityToComplete.getSchedule().get(),
	                    activityToComplete.getFrequency().get());
	        	try {
					task.status = new Status(STATUS_COMPLETE);
				} catch (IllegalValueException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            afterComplete = new Activity(task);
	        }
        }
        		
        try {
            model.deleteActivity(activityToComplete);
            model.addActivity(afterComplete);
            return new CommandResult(String.format(MESSAGE_SUCCESS, afterComplete));
        } catch (UniqueActivityList.ActivityNotFoundException pnfe) {

            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);

        } catch (UniqueActivityList.DuplicateActivityException e) {

            try {
                model.addActivity(afterComplete);
            } catch (UniqueActivityList.DuplicateActivityException e1) {
                assert false: "Deleted activity should be able to be added back.";
            }
            return new CommandResult(MESSAGE_DUPLICATE_TASK);
        }
	}

}
