file:///C:/Users/mario/EquipeFoot/shared/src/main/scala/apps/EDF/Types.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -SquadSize.
	 -scala/Predef.SquadSize.
offset: 942
uri: file:///C:/Users/mario/EquipeFoot/shared/src/main/scala/apps/EDF/Types.scala
text:
```scala
import java.text.Normalizer.Form

enum Position:
    case G
    case DD
    case DG
    case DC
    case MDC
    case MC
    case MOC
    case MG
    case MD
    case BU
    case AD
    case AG

enum Role :
    case Entraineur
    case Assistant
    case Capitaine

enum SquadSize:
    case Sept,Neuf,Onze

    def nbPlayers: Int = this match
        case Sept => 7
        case Neuf => 9
        case Onze => 11

final case class FormationId(value : String) extends AnyVal

final case class Formation(
    id: FormationId,
    label: String,
    taille: SquadSize,
    defenseurs: Int,
    milieux:Int,
    attaquants: Int
):
    require(
        1 + defenseurs + milieux + attaquants == taille.nbPlayers
    )

object Formation:

    val elevenAside : List[Formation] = List(
        Formation(FormationId("5-4-1"),"5-4-1",SquadSize.Onze,5,4,1),
        Formation(FormationId("5-3-2"),"5-3-2",@@SquadSize.Onze,5,3,2),
        Formation(FormationId("5-2-3"),"5-2-3",11,5,2,3),
        Formation(FormationId("4-5-1"),"4-5-1",11,4,5,1),
        Formation(FormationId("4-4-2"),"4-4-2",11,4,4,2),
        Formation(FormationId("4-3-3"),"4-3-3",11,4,3,3),
        Formation(FormationId("4-3-3"),"4-3-3",11,4,3,3),
        Formation(FormationId("4-2-4"),"4-2-4",11,4,2,4),
        Formation(FormationId("3-5-2"),"3-5-2",11,3,5,2),
        Formation(FormationId("3-4-3"),"3-4-3",11,3,4,3)
    )

    val nineAside : List[Formation] = List(
        Formation(FormationId("5-2-1"),"5-2-1",9,5,2,1),
        Formation(FormationId("5-1-2"),"5-1-2",9,5,1,2),
        Formation(FormationId("4-3-1"),"4-3-1",9,4,3,1),
        Formation(FormationId("4-2-2"),"4-2-2",9,4,2,2),
        Formation(FormationId("4-1-3"),"4-1-3",9,4,1,3),
        Formation(FormationId("3-4-1"),"3-4-1",9,3,4,1),
        Formation(FormationId("3-3-2"),"3-3-2",9,3,3,2),
        Formation(FormationId("3-2-3"),"3-2-3",9,3,2,3),
        Formation(FormationId("3-1-4"),"3-1-4",9,3,1,4),
        Formation(FormationId("2-3-3"),"2-3-3",9,2,3,3),
        Formation(FormationId("2-4-2"),"2-4-2",9,2,4,2)
    )

    val sevenAside : List[Formation] = List(
        Formation(FormationId("3-1-2"),"3-1-2",7,3,1,2),
        Formation(FormationId("3-2-1"),"3-2-1",7,3,2,1),
        Formation(FormationId("2-3-1"),"2-3-1",7,2,3,1),
        Formation(FormationId("2-2-2"),"2-2-2",7,2,2,2)
    )

    val all:List[Formation] = elevenAside ++ nineAside ++ sevenAside


case class PlayerId(value : String) extends AnyVal
case class TeamId(value : String) extends AnyVal

final case class Player(
    id : PlayerId,
    name : String,
    number : Int,
    prefferedPosition : Position,
    otherPositions : Seq[Position],
    role : Set[Role] = Set.empty
)

final case class StaffMember(
    name : String,
    role : Role
)

final case class Team(
    id : TeamId,
    name : String,
    players : Seq[Player],
    nbPlayers : Int,
    staff : Seq[StaffMember],
    capitaine : Option[PlayerId] = None
):
    require(players.nonEmpty, "Une équipe doit avoir au moins un joueur")
    require(players.map(_.id).distinct.size == players.size, "Ids joueurs dupliqués interdits")

enum Line:
  case Goalkeeper
  case Defense
  case Midfield
  case Attack

final case class SlotId(value: Int) extends AnyVal

final case class LineupSlot(
    id: SlotId,
    line: Line,
    indexInLine: Int,
    expectedPosition: Position,
    player: Option[PlayerId]
)

final case class Lineup(
    teamId: TeamId,
    formation: Formation,
    slots: List[LineupSlot]
):
  
  def size: SquadSize = formation.taille

  def playersOnField: List[PlayerId] =
    slots.flatMap(_.player)

  def isComplete: Boolean =
    playersOnField.distinct.size == formation.taille.nbPlayers
```


#### Short summary: 

empty definition using pc, found symbol in pc: 