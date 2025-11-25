error id: file:///C:/Users/mario/EquipeFoot/apps/shared/src/main/scala/apps/EDF/Wires.scala:`<none>`.
file:///C:/Users/mario/EquipeFoot/apps/shared/src/main/scala/apps/EDF/Wires.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb

found definition using fallback; symbol String
offset: 410
uri: file:///C:/Users/mario/EquipeFoot/apps/shared/src/main/scala/apps/EDF/Wires.scala
text:
```scala
package apps.EDF

import upickle.default.*
import upickle.default.ReadWriter.join

final case class AppState(
    teams: Seq[Team],
    selectedTeam: Option[TeamId],
    currentLineup: Option[Lineup]
)


enum ClientToServer:

  case CreateTeam(name: String, nbPlayers: Int)
  case DeleteTeam(teamId: TeamId)

  case AddPlayer(
      teamId: TeamId,
      playerId: String,
      name: Strin@@g,
      number: Int,
      preferred: Position,
      others: Seq[Position]
  )
  case RemovePlayer(teamId: TeamId, playerId: PlayerId)
  case SetCaptain(teamId: TeamId, captain: Option[PlayerId])

  case SelectTeam(teamId: TeamId)
  case SetFormation(teamId: TeamId, formationId: FormationId)
  case PlacePlayerInSlot(teamId: TeamId, slotId: SlotId, playerId: Option[PlayerId])
  case ClearLineup(teamId: TeamId)

enum ServerToClient:
  case StateUpdated(state: AppState)
  case Error(message: String)

given ReadWriter[Position]     = macroRW
given ReadWriter[Role]         = macroRW
given ReadWriter[SquadSize]    = macroRW
given ReadWriter[FormationId]  = macroRW
given ReadWriter[Formation]    = macroRW
given ReadWriter[PlayerId]     = macroRW
given ReadWriter[TeamId]       = macroRW
given ReadWriter[Player]       = macroRW
given ReadWriter[StaffMember]  = macroRW
given ReadWriter[Team]         = macroRW
given ReadWriter[Line]         = macroRW
given ReadWriter[SlotId]       = macroRW
given ReadWriter[LineupSlot]   = macroRW
given ReadWriter[Lineup]       = macroRW
given ReadWriter[AppState]     = macroRW
given ReadWriter[ClientToServer] = macroRW
given ReadWriter[ServerToClient] = macroRW

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.