package nl.codecentric.inspection.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventTag,
  PersistentEntity
}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{
  JsonSerializer,
  JsonSerializerRegistry
}
import play.api.libs.json.{Format, Json, Reads, Writes}

import scala.collection.immutable.Seq

class InspectionEntity extends PersistentEntity {

  override type Command = InspectionCommand[_]
  override type Event = InspectionEvent
  override type State = InspectionState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: InspectionState =
    InspectionState(List.empty)

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case InspectionState(votes) =>
      Actions()
        .onCommand[AddFrameworkVote, Done] {

          // Command handler for the UseGreetingMessage command
          case (AddFrameworkVote(score, comment), ctx, state) =>
            // In response to this command, we want to first persist it as a
            // GreetingMessageChanged event
            ctx.thenPersist(
              FrameworkVotedAddition(entityId, score, comment)
            ) { _ =>
              // Then once the event is successfully persisted, we respond with done.
              ctx.reply(Done)
            }

        }
        .onReadOnlyCommand[FrameworkVoting, String] {

          case (FrameworkVoting(), ctx, state) =>
            ctx.reply(s"${state.votes.mkString(" | ")}")

        }
        .onEvent {

          // Event handler for the GreetingMessageChanged event
          case (FrameworkVotedAddition(name, score, comment), state) =>
            // We simply update the current state to use the greeting message from
            // the event.
            InspectionState(Vote(score, comment) :: state.votes)
        }
  }
}

/**
  * The current state held by the persistent entity.
  */
case class InspectionState(votes: List[Vote])

object InspectionState {

  implicit val format: Format[InspectionState] = Json.format
}

case class Vote(score: Int, comment: Option[String] = None)

object Vote {
  implicit val format: Format[Vote] = Json.format
}

/**
  * This interface defines all the events that the InspectionEntity supports.
  */
object InspectionEvent {
  val InspectionEventTag = AggregateEventTag[InspectionEvent]
}

sealed trait InspectionEvent extends AggregateEvent[InspectionEvent] {
  override def aggregateTag: AggregateEventTag[InspectionEvent] =
    InspectionEvent.InspectionEventTag
}

case class FrameworkVotedAddition(framework: String,
                                  score: Int,
                                  comment: Option[String] = None)
    extends InspectionEvent

object FrameworkVotedAddition {
  implicit val format: Format[FrameworkVotedAddition] = Json.format
}

/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
sealed trait InspectionCommand[R] extends ReplyType[R]

case class AddFrameworkVote(score: Int, comment: Option[String] = None)
    extends InspectionCommand[Done]

object AddFrameworkVote {

  implicit val format: Format[AddFrameworkVote] = Json.format
}

case class FrameworkVoting() extends InspectionCommand[String]

object FrameworkVoting {

//  implicit val reader: Reads[FrameworkVoting] = ("FrameworkVoting").read[FrameworkVoting]
//  implicit val writer = Writes[FrameworkVoting}{
//    Json.toJson()(Foo.fmt)
//}
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object InspectionSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[FrameworkVotedAddition],
    JsonSerializer[AddFrameworkVote],
    JsonSerializer[Vote],
    JsonSerializer[InspectionState]
  )
}
