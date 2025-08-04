import com.google.inject.AbstractModule
import play.api.http.HttpFilters
import utils.Startup

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Startup]).asEagerSingleton()
  }
}
