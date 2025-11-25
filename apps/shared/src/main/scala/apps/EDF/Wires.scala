package apps.EDF

import upickle.default.*

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
      name: String,
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

//
// 1. Enums simples encodés comme des chaînes
//

given ReadWriter[Position] =
  readwriter[String].bimap[Position](
    p => p.toString,
    s => Position.valueOf(s)
  )

given ReadWriter[Role] =
  readwriter[String].bimap[Role](
    r => r.toString,
    s => Role.valueOf(s)
  )

given ReadWriter[SquadSize] =
  readwriter[String].bimap[SquadSize](
    s => s.toString,
    s => SquadSize.valueOf(s)
  )

given ReadWriter[Line] =
  readwriter[String].bimap[Line](
    l => l.toString,
    s => Line.valueOf(s)
  )

//
// 2. Value classes encodées via leur valeur interne
//

given ReadWriter[FormationId] =
  readwriter[String].bimap[FormationId](
    id => id.value,
    s  => FormationId(s)
  )

given ReadWriter[PlayerId] =
  readwriter[String].bimap[PlayerId](
    id => id.value,
    s  => PlayerId(s)
  )

given ReadWriter[TeamId] =
  readwriter[String].bimap[TeamId](
    id => id.value,
    s  => TeamId(s)
  )

given ReadWriter[SlotId] =
  readwriter[Int].bimap[SlotId](
    id => id.value,
    i  => SlotId(i)
  )

//
// 3. Case classes encodées comme objets JSON
//

given ReadWriter[Formation] =
  readwriter[ujson.Obj].bimap[Formation](
    f =>
      ujson.Obj(
        "id"         -> writeJs(f.id),
        "label"      -> f.label,
        "taille"     -> writeJs(f.taille),
        "defenseurs" -> f.defenseurs,
        "milieux"    -> f.milieux,
        "attaquants" -> f.attaquants
      ),
    obj =>
      Formation(
        id         = read[FormationId](obj("id")),
        label      = obj("label").str,
        taille     = read[SquadSize](obj("taille")),
        defenseurs = obj("defenseurs").num.toInt,
        milieux    = obj("milieux").num.toInt,
        attaquants = obj("attaquants").num.toInt
      )
  )

given ReadWriter[Player] =
  readwriter[ujson.Obj].bimap[Player](
    p =>
      ujson.Obj(
        "id"               -> writeJs(p.id),
        "name"             -> p.name,
        "number"           -> p.number,
        "prefferedPosition"-> writeJs(p.prefferedPosition),
        "otherPositions"   -> writeJs(p.otherPositions),
        "role"             -> writeJs(p.role.toSeq)
      ),
    obj =>
      Player(
        id               = read[PlayerId](obj("id")),
        name             = obj("name").str,
        number           = obj("number").num.toInt,
        prefferedPosition= read[Position](obj("prefferedPosition")),
        otherPositions   = read[Seq[Position]](obj("otherPositions")),
        role             = read[Seq[Role]](obj("role")).toSet
      )
  )

given ReadWriter[StaffMember] =
  readwriter[ujson.Obj].bimap[StaffMember](
    s =>
      ujson.Obj(
        "name" -> s.name,
        "role" -> writeJs(s.role)
      ),
    obj =>
      StaffMember(
        name = obj("name").str,
        role = read[Role](obj("role"))
      )
  )

given ReadWriter[Team] =
  readwriter[ujson.Obj].bimap[Team](
    t =>
      ujson.Obj(
        "id"        -> writeJs(t.id),
        "name"      -> t.name,
        "players"   -> writeJs(t.players),
        "nbPlayers" -> t.nbPlayers,
        "staff"     -> writeJs(t.staff),
        "capitaine" -> writeJs(t.capitaine)
      ),
    obj =>
      Team(
        id        = read[TeamId](obj("id")),
        name      = obj("name").str,
        players   = read[Seq[Player]](obj("players")),
        nbPlayers = obj("nbPlayers").num.toInt,
        staff     = read[Seq[StaffMember]](obj("staff")),
        capitaine = read[Option[PlayerId]](obj("capitaine"))
      )
  )

given ReadWriter[LineupSlot] =
  readwriter[ujson.Obj].bimap[LineupSlot](
    s =>
      ujson.Obj(
        "id"              -> writeJs(s.id),
        "line"            -> writeJs(s.line),
        "indexInLine"     -> s.indexInLine,
        "expectedPosition"-> writeJs(s.expectedPosition),
        "player"          -> writeJs(s.player)
      ),
    obj =>
      LineupSlot(
        id              = read[SlotId](obj("id")),
        line            = read[Line](obj("line")),
        indexInLine     = obj("indexInLine").num.toInt,
        expectedPosition= read[Position](obj("expectedPosition")),
        player          = read[Option[PlayerId]](obj("player"))
      )
  )

given ReadWriter[Lineup] =
  readwriter[ujson.Obj].bimap[Lineup](
    l =>
      ujson.Obj(
        "teamId"    -> writeJs(l.teamId),
        "formation" -> writeJs(l.formation),
        "slots"     -> writeJs(l.slots)
      ),
    obj =>
      Lineup(
        teamId    = read[TeamId](obj("teamId")),
        formation = read[Formation](obj("formation")),
        slots     = read[List[LineupSlot]](obj("slots"))
      )
  )

given ReadWriter[AppState] =
  readwriter[ujson.Obj].bimap[AppState](
    s =>
      ujson.Obj(
        "teams"        -> writeJs(s.teams),
        "selectedTeam" -> writeJs(s.selectedTeam),
        "currentLineup"-> writeJs(s.currentLineup)
      ),
    obj =>
      AppState(
        teams         = read[Seq[Team]](obj("teams")),
        selectedTeam  = read[Option[TeamId]](obj("selectedTeam")),
        currentLineup = read[Option[Lineup]](obj("currentLineup"))
      )
  )

//
// 4. ADT ClientToServer / ServerToClient encodés comme objets taggés
//

given ReadWriter[ClientToServer] =
  readwriter[ujson.Obj].bimap[ClientToServer](
    {
      case ClientToServer.CreateTeam(name, nbPlayers) =>
        ujson.Obj(
          "tag"       -> "CreateTeam",
          "name"      -> name,
          "nbPlayers" -> nbPlayers
        )

      case ClientToServer.DeleteTeam(teamId) =>
        ujson.Obj(
          "tag"    -> "DeleteTeam",
          "teamId" -> writeJs(teamId)
        )

      case ClientToServer.AddPlayer(teamId, playerId, name, number, preferred, others) =>
        ujson.Obj(
          "tag"       -> "AddPlayer",
          "teamId"    -> writeJs(teamId),
          "playerId"  -> playerId,
          "name"      -> name,
          "number"    -> number,
          "preferred" -> writeJs(preferred),
          "others"    -> writeJs(others)
        )

      case ClientToServer.RemovePlayer(teamId, playerId) =>
        ujson.Obj(
          "tag"      -> "RemovePlayer",
          "teamId"   -> writeJs(teamId),
          "playerId" -> writeJs(playerId)
        )

      case ClientToServer.SetCaptain(teamId, captain) =>
        ujson.Obj(
          "tag"     -> "SetCaptain",
          "teamId"  -> writeJs(teamId),
          "captain" -> writeJs(captain)
        )

      case ClientToServer.SelectTeam(teamId) =>
        ujson.Obj(
          "tag"    -> "SelectTeam",
          "teamId" -> writeJs(teamId)
        )

      case ClientToServer.SetFormation(teamId, formationId) =>
        ujson.Obj(
          "tag"         -> "SetFormation",
          "teamId"      -> writeJs(teamId),
          "formationId" -> writeJs(formationId)
        )

      case ClientToServer.PlacePlayerInSlot(teamId, slotId, playerId) =>
        ujson.Obj(
          "tag"      -> "PlacePlayerInSlot",
          "teamId"   -> writeJs(teamId),
          "slotId"   -> writeJs(slotId),
          "playerId" -> writeJs(playerId)
        )

      case ClientToServer.ClearLineup(teamId) =>
        ujson.Obj(
          "tag"    -> "ClearLineup",
          "teamId" -> writeJs(teamId)
        )
    },
    { obj =>
      obj("tag").str match
        case "CreateTeam" =>
          ClientToServer.CreateTeam(
            name      = obj("name").str,
            nbPlayers = obj("nbPlayers").num.toInt
          )

        case "DeleteTeam" =>
          ClientToServer.DeleteTeam(
            teamId = read[TeamId](obj("teamId"))
          )

        case "AddPlayer" =>
          ClientToServer.AddPlayer(
            teamId    = read[TeamId](obj("teamId")),
            playerId  = obj("playerId").str,
            name      = obj("name").str,
            number    = obj("number").num.toInt,
            preferred = read[Position](obj("preferred")),
            others    = read[Seq[Position]](obj("others"))
          )

        case "RemovePlayer" =>
          ClientToServer.RemovePlayer(
            teamId   = read[TeamId](obj("teamId")),
            playerId = read[PlayerId](obj("playerId"))
          )

        case "SetCaptain" =>
          ClientToServer.SetCaptain(
            teamId  = read[TeamId](obj("teamId")),
            captain = read[Option[PlayerId]](obj("captain"))
          )

        case "SelectTeam" =>
          ClientToServer.SelectTeam(
            teamId = read[TeamId](obj("teamId"))
          )

        case "SetFormation" =>
          ClientToServer.SetFormation(
            teamId      = read[TeamId](obj("teamId")),
            formationId = read[FormationId](obj("formationId"))
          )

        case "PlacePlayerInSlot" =>
          ClientToServer.PlacePlayerInSlot(
            teamId   = read[TeamId](obj("teamId")),
            slotId   = read[SlotId](obj("slotId")),
            playerId = read[Option[PlayerId]](obj("playerId"))
          )

        case "ClearLineup" =>
          ClientToServer.ClearLineup(
            teamId = read[TeamId](obj("teamId"))
          )
    }
  )

given ReadWriter[ServerToClient] =
  readwriter[ujson.Obj].bimap[ServerToClient](
    {
      case ServerToClient.StateUpdated(state) =>
        ujson.Obj(
          "tag"   -> "StateUpdated",
          "state" -> writeJs(state)
        )

      case ServerToClient.Error(message) =>
        ujson.Obj(
          "tag"     -> "Error",
          "message" -> message
        )
    },
    { obj =>
      obj("tag").str match
        case "StateUpdated" =>
          ServerToClient.StateUpdated(
            state = read[AppState](obj("state"))
          )

        case "Error" =>
          ServerToClient.Error(
            message = obj("message").str
          )
    }
  )
