package seedu.taskman.model;

import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.UniqueActivityList;

import java.util.Set;

/**
 * The API of the Model component.
 */
public interface Model {

    public enum FilterMode {
        SCHEDULE_ONLY,
        DEADLINE_ONLY,
        FLOATING_ONLY,
        ALL
    }

    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskMan newData);

    /** Returns the TaskMan */
    ReadOnlyTaskMan getTaskMan();

    /** Deletes the given activity. */
    void deleteActivity(Activity target) throws UniqueActivityList.ActivityNotFoundException;

    //TODO Is this even needed?
    /** Adds the given event */
    void addEvent(Event task) throws UniqueActivityList.DuplicateActivityException;

    void addActivity(Activity activity) throws  UniqueActivityList.DuplicateActivityException;

    /** Returns the filtered task list as an {@code UnmodifiableObservableList<Activity>} */
    UnmodifiableObservableList<Activity> getFilteredActivityList();

    /** Updates the filter of the filtered activity list to show all activities */
    void updateFilteredListToShowAll();

    /** Updates the filter of the filtered activity list to filter by the given mode, the given keywords and the given tag names*/
    void updateFilteredActivityList(FilterMode filterMode, Set<String> keywords, Set<String> tagNames);

}
