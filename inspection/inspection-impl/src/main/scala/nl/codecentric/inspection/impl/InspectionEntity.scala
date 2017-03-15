package nl.codecentric.inspection.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

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
    case InspectionState(inspections) =>
      Actions()
        .onCommand[AddInspectionCommand, Done] {

          case (AddInspectionCommand(ucrn, dtInspection, employee, remarks), ctx, state) =>
            // In response to this command, we want to first persist it as an InspectionAddedEvent
            ctx.thenPersist(
              InspectionAddedEvent(ucrn, dtInspection, employee, remarks)
            ) { _ =>
              // Then once the event is successfully persisted, we respond with done.
              ctx.reply(Done)
            }

        }
        .onReadOnlyCommand[RequestAllInspections, Set[Inspection]] {
          case (RequestAllInspections(), ctx, state) =>
            ctx.reply(state.inspections.toSet)
        }
        .onReadOnlyCommand[RequestInspectionsByEmployeeCommand, Set[Inspection]] {
          case (RequestInspectionsByEmployeeCommand(employee), ctx, state) =>
            ctx.reply(state.inspections.filter(i => employee.equalsIgnoreCase(i.employee.orNull)).toSet)
        }
        .onEvent {
          // Event handler for the InspectionAddedEvent event
          case (InspectionAddedEvent(ucrn, dtInspection, employee, remarks), state) =>
            // We simply update the current state to add the new inspection
            InspectionState(Inspection(ucrn, dtInspection, employee, remarks) :: state.inspections)
        }
  }
}

/**
  * The current state held by the persistent entity.
  */
case class InspectionState(inspections: List[Inspection])

object InspectionState {

  implicit val format: Format[InspectionState] = Json.format
}

case class Inspection(ucrn: String,
                      dtInspection: Option[DateTime] = None,
                      employee: Option[String] = None,
                      remarks: Option[String] = None)

object Inspection {
  implicit val format: Format[Inspection] = Json.format
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

case class InspectionAddedEvent(ucrn: String,
                                dtInspection: Option[DateTime] = None,
                                employee: Option[String] = None,
                                remarks: Option[String] = None)
    extends InspectionEvent

object InspectionAddedEvent {
  implicit val format: Format[InspectionAddedEvent] = Json.format
}

/**
  * This interface defines all the commands that the Inspection entity supports.
  */
sealed trait InspectionCommand[R] extends ReplyType[R]

case class AddInspectionCommand(ucrn: String,
                                dtInspection: Option[DateTime] = None,
                                employee: Option[String] = None,
                                remarks: Option[String] = None)
  extends InspectionCommand[Done]

object AddInspectionCommand {
  implicit val format: Format[AddInspectionCommand] = Json.format
}

case class RequestAllInspections()
  extends InspectionCommand[Set[Inspection]]

object RequestAllInspections {
}

case class RequestInspectionsByEmployeeCommand(employee: String)
  extends InspectionCommand[Set[Inspection]]

object RequestInspectionsByEmployeeCommand {
  implicit val format: Format[RequestInspectionsByEmployeeCommand] = Json.format
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
    JsonSerializer[InspectionAddedEvent],
    JsonSerializer[AddInspectionCommand],
    JsonSerializer[RequestInspectionsByEmployeeCommand],
    JsonSerializer[Inspection],
    JsonSerializer[InspectionState]
  )
}
