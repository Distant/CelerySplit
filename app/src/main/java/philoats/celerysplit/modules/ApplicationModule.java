package philoats.celerysplit.modules;

import philoats.celerysplit.activities.MainActivity;
import philoats.celerysplit.presenters.TimerPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {MainActivity.class}
)

public class ApplicationModule {

    public ApplicationModule() {
    }

    @Provides @Singleton TimerPresenter provideTitleController() {
        return new TimerPresenter();
    }
}