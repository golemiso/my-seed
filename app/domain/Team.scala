package domain

case class Team(id: TeamID, players: Seq[Player]) extends Entity[TeamID]

case class TeamID(value: String) extends Identity
