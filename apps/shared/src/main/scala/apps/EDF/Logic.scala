package apps.EDF

import scala.util.Random

object Logic:

  private val rng = new Random()

    private def newId(prefix: String): String =
    val n = math.abs(rng.nextLong())
    s"$prefix-${java.lang.Long.toString(n, 36)}"


  def init: ServerToClient =
    val state = AppState(
      teams = Seq.empty,
      selectedTeam = None,
      currentLineup = None
    )
    ServerToClient.StateUpdated(state)

  
  def update(state: AppState, msg: ClientToServer): ServerToClient =
    msg match
      
      case ClientToServer.CreateTeam(name, nbPlayers) =>
        val Id   = TeamId(newId("team"))
        val newTeam = Team(
          id = Id,
          name = name,
          players = Seq.empty,
          nbPlayers = nbPlayers,
          staff = Seq.empty,
          capitaine = None
        )
        val newState = state.copy(
          teams = state.teams :+ newTeam,
          selectedTeam = Some(Id),
          currentLineup = None
        )
        ServerToClient.StateUpdated(newState)

      case ClientToServer.DeleteTeam(teamId) =>
        if !state.teams.exists(_.id == teamId) then
          ServerToClient.Error(s"Equipe inconnue: ${teamId.value}")
        else
          val remainingTeams = state.teams.filterNot(_.id == teamId)
          val newSelected =
            state.selectedTeam match
              case Some(id) if id == teamId => None
              case other                    => other
          val newLineup =
            state.currentLineup match
              case Some(lu) if lu.teamId == teamId => None
              case other                           => other
          val newState = state.copy(
            teams = remainingTeams,
            selectedTeam = newSelected,
            currentLineup = newLineup
          )
          ServerToClient.StateUpdated(newState)

      
      case ClientToServer.AddPlayer(teamId, rawPlayerId, name, number, preferred, others) =>
        state.teams.find(_.id == teamId) match
          case None =>
            ServerToClient.Error(s"Equipe inconnue: ${teamId.value}")

          case Some(team) =>
            if team.players.exists(_.id.value == rawPlayerId) then
              ServerToClient.Error(s"Identifiant joueur déjà utilisé: $rawPlayerId")
            else if team.players.size >= team.nbPlayers then
              ServerToClient.Error(s"Effectif complet pour l'équipe ${team.name}")
            else
              val player = Player(
                id = PlayerId(rawPlayerId),
                name = name,
                number = number,
                prefferedPosition = preferred,
                otherPositions = others,
                role = Set.empty
              )
              val updatedTeam = team.copy(players = team.players :+ player)
              val newTeams = state.teams.map(t => if t.id == teamId then updatedTeam else t)
              val newState = state.copy(teams = newTeams)
              ServerToClient.StateUpdated(newState)

      case ClientToServer.RemovePlayer(teamId, playerId) =>
        state.teams.find(_.id == teamId) match
          case None =>
            ServerToClient.Error(s"Equipe inconnue: ${teamId.value}")

          case Some(team) =>
            if !team.players.exists(_.id == playerId) then
              ServerToClient.Error(s"Joueur inconnu dans l'équipe: ${playerId.value}")
            else
              val updatedTeam = team.copy(players = team.players.filterNot(_.id == playerId))
              val newTeams = state.teams.map(t => if t.id == teamId then updatedTeam else t)

              
              val newLineup =
                state.currentLineup match
                  case Some(lu) if lu.teamId == teamId =>
                    val cleanedSlots = lu.slots.map { slot =>
                      if slot.player.contains(playerId) then slot.copy(player = None)
                      else slot
                    }
                    Some(lu.copy(slots = cleanedSlots))
                  case other => other

              val newState = state.copy(teams = newTeams, currentLineup = newLineup)
              ServerToClient.StateUpdated(newState)

      case ClientToServer.SetCaptain(teamId, captainOpt) =>
        state.teams.find(_.id == teamId) match
          case None =>
            ServerToClient.Error(s"Equipe inconnue: ${teamId.value}")

          case Some(team) =>
            captainOpt match
              case Some(cId) if !team.players.exists(_.id == cId) =>
                ServerToClient.Error(s"Capitaine inconnu dans l'équipe: ${cId.value}")

              case _ =>
                val updatedTeam = team.copy(capitaine = captainOpt)
                val newTeams = state.teams.map(t => if t.id == teamId then updatedTeam else t)
                val newState = state.copy(teams = newTeams)
                ServerToClient.StateUpdated(newState)

      
      case ClientToServer.SelectTeam(teamId) =>
        if !state.teams.exists(_.id == teamId) then
          ServerToClient.Error(s"Equipe inconnue: ${teamId.value}")
        else
          val newLineup =
            state.currentLineup match
              case Some(lu) if lu.teamId == teamId => Some(lu)
              case _                               => None
          val newState = state.copy(
            selectedTeam = Some(teamId),
            currentLineup = newLineup
          )
          ServerToClient.StateUpdated(newState)

      case ClientToServer.SetFormation(teamId, formationId) =>
        state.teams.find(_.id == teamId) match
          case None =>
            ServerToClient.Error(s"Equipe inconnue: ${teamId.value}")

          case Some(_) =>
            Formation.all.find(_.id == formationId) match
              case None =>
                ServerToClient.Error(s"Formation inconnue: ${formationId.value}")

              case Some(formation) =>
                val lineup = emptyLineup(teamId, formation)
                val newState = state.copy(
                  selectedTeam = Some(teamId),
                  currentLineup = Some(lineup)
                )
                ServerToClient.StateUpdated(newState)

      case ClientToServer.PlacePlayerInSlot(teamId, slotId, playerIdOpt) =>
        state.teams.find(_.id == teamId) match
          case None =>
            ServerToClient.Error(s"Equipe inconnue: ${teamId.value}")

          case Some(team) =>
            
            playerIdOpt match
              case Some(pid) if !team.players.exists(_.id == pid) =>
                ServerToClient.Error(s"Joueur inconnu dans l'équipe: ${pid.value}")

              case _ =>
                state.currentLineup match
                  case None =>
                    ServerToClient.Error("Aucune compo active pour cette équipe")

                  case Some(lineup) if lineup.teamId != teamId =>
                    ServerToClient.Error("La compo actuelle appartient à une autre équipe")

                  case Some(lineup) =>
                    
                    val withoutPlayer =
                      playerIdOpt match
                        case Some(pid) =>
                          lineup.slots.map { s =>
                            if s.player.contains(pid) then s.copy(player = None)
                            else s
                          }
                        case None =>
                          lineup.slots

            
                    val updatedSlots = withoutPlayer.map { s =>
                      if s.id == slotId then s.copy(player = playerIdOpt)
                      else s
                    }

                    val newLineup = lineup.copy(slots = updatedSlots)
                    val newState  = state.copy(currentLineup = Some(newLineup))
                    ServerToClient.StateUpdated(newState)

      case ClientToServer.ClearLineup(teamId) =>
        state.currentLineup match
          case Some(lineup) if lineup.teamId == teamId =>
            val cleared = lineup.copy(
              slots = lineup.slots.map(_.copy(player = None))
            )
            val newState = state.copy(currentLineup = Some(cleared))
            ServerToClient.StateUpdated(newState)

          case _ =>
            
            ServerToClient.StateUpdated(state)
            
  def end(state: AppState): ServerToClient =
    ServerToClient.StateUpdated(state)

  private def emptyLineup(teamId: TeamId, formation: Formation): Lineup =
    val gkSlot =
      LineupSlot(
        id = SlotId(0),
        line = Line.Goalkeeper,
        indexInLine = 0,
        expectedPosition = Position.G,
        player = None
      )

    val defenderSlots =
      (0 until formation.defenseurs).toList.map { i =>
        LineupSlot(
          id = SlotId(1 + i),
          line = Line.Defense,
          indexInLine = i,
          expectedPosition = Position.DC,
          player = None
        )
      }

    val midStart = 1 + formation.defenseurs
    val midfieldSlots =
      (0 until formation.milieux).toList.map { i =>
        LineupSlot(
          id = SlotId(midStart + i),
          line = Line.Midfield,
          indexInLine = i,
          expectedPosition = Position.MC,
          player = None
        )
      }

    val attStart = midStart + formation.milieux
    val attackSlots =
      (0 until formation.attaquants).toList.map { i =>
        LineupSlot(
          id = SlotId(attStart + i),
          line = Line.Attack,
          indexInLine = i,
          expectedPosition = Position.BU,
          player = None
        )
      }

    Lineup(
      teamId = teamId,
      formation = formation,
      slots = gkSlot :: (defenderSlots ++ midfieldSlots ++ attackSlots)
    )
