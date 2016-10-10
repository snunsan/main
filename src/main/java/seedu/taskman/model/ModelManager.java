package seedu.taskman.model;

import javafx.collections.transformation.FilteredList;
import seedu.taskman.commons.core.ComponentManager;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.commons.events.model.TaskManChangedEvent;
import seedu.taskman.commons.util.StringUtil;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.UniqueActivityList;
import seedu.taskman.model.event.UniqueActivityList.ActivityNotFoundException;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the task man data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskMan taskMan;
    private final FilteredList<Activity> filteredActivities;

    /**
     * Initializes a ModelManager with the given TaskMan
     * TaskMan and its variables should not be null
     */
    public ModelManager(TaskMan src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

        logger.fine("Initializing with Task Man: " + src + " and user prefs " + userPrefs);

        taskMan = new TaskMan(src);
        filteredActivities = new FilteredList<>(taskMan.getActivities());
    }

    public ModelManager() {
        this(new TaskMan(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskMan initialData, UserPrefs userPrefs) {
        taskMan = new TaskMan(initialData);
        filteredActivities = new FilteredList<>(taskMan.getActivities());
    }

    @Override
    public void resetData(ReadOnlyTaskMan newData) {
        taskMan.resetData(newData);
        indicateTaskManChanged();
    }

    public ReadOnlyTaskMan getTaskMan() {
        return taskMan;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateTaskManChanged() {
        raise(new TaskManChangedEvent(taskMan));
    }

    @Override
    public synchronized void deleteActivity(Activity target) throws ActivityNotFoundException {
        taskMan.removeActivity(target);
        indicateTaskManChanged();
    }

    @Override
    public synchronized void addTask(Task task) throws UniqueActivityList.DuplicateActivityException {
        taskMan.addTask(task);
        updateFilteredListToShowAll();
        indicateTaskManChanged();
    }

    @Override
    public synchronized void addActivity(Activity activity) throws UniqueActivityList.DuplicateActivityException {
        taskMan.addActivity(activity);
        updateFilteredListToShowAll();
        indicateTaskManChanged();
    }

    //=========== Filtered Task List Accessors ===============================================================

    @Override
    public UnmodifiableObservableList<Activity> getFilteredActivityList() {
        return new UnmodifiableObservableList<>(filteredActivities);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredActivities.setPredicate(null);
    }

    @Override
    public void updateFilteredActivityList(Set<String> keywords){
        updateFilteredTaskList(new PredicateExpression(new TitleQualifier(keywords)));
    }

    private void updateFilteredTaskList(Expression expression) {
        filteredActivities.setPredicate(expression::satisfies);
    }

    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(Activity task);
        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(Activity activity) {
            return qualifier.run(activity);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(Activity activity);
        String toString();
    }

    private class TitleQualifier implements Qualifier {
        private Set<String> titleKeyWords;

        TitleQualifier(Set<String> titleKeyWords) {
            this.titleKeyWords = titleKeyWords;
        }

        @Override
        public boolean run(Activity activity) {
            return titleKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(activity.getTitle().title, keyword))
                    .findAny()
                    .isPresent();
        }

        @Override
        public String toString() {
            return "title=" + String.join(", ", titleKeyWords);
        }
    }

}
