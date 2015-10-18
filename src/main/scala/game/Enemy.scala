package com.github.fellowship_of_the_bus
package eshe
package game
import IDMap._
import lib.game.GameConfig.{Width}
import lib.ui.{Drawable}

import java.util.Scanner
import java.io.File

import lib.util.rand

trait EnemyType extends CharacterType {

}

object Enemy {
  private lazy val names = read("data/names.txt")
  private lazy val facts = read("data/true-facts.txt")

  def read(filename: String): List[String] = {
    var lst = List[String]()

    val stream = ElderStrolls.getClass.getClassLoader().getResourceAsStream(filename)
    val sc = new Scanner(stream)
    while (sc.hasNextLine) {
      lst = sc.nextLine::lst 
    }
    lst
  }

  def name() = names(rand(names.length-1))
  def fact() = facts(rand(facts.length-1))  
}

abstract class Enemy(xc: Float, yc: Float, override val base: EnemyType) extends game.Character(xc, yc, base) {
  val age: Int = rand(6, 12)

  val name = Enemy.name
  val fact = Enemy.fact
  var flying = false
  var knockTicks = 0
  var knockVelocity = 0f

  var target: Player = null
  override def update(delta: Long, game: Game) = {
    super.update(delta, game)

    // Knocked back behaviour
    if (flying) {
      knockTicks -= 1
      x = x + knockVelocity
      if (knockTicks <= 0) {
        flying = false
      }
    } else {
      if (target == null) {
        target = game.players(rand(game.maxPlayers))
      } else {
        if (false) {
          // if target is in range, attack
        } else {
          // otherwise move until enemy is in range
          move
        }
      }
    }
  }

  def move() = {
    
  }

  def knockback(distance: Float) {
    knockVelocity = distance / 30
    knockTicks = 30
  }
}

object Ghost extends EnemyType {
  val id = GhostW1ID
  val maxHp = 10
  val attack = 2
  val defense = 1 
  val speed = 1
}

class Ghost(xc: Float, yc: Float) extends Enemy(xc, yc, Ghost) {
  val walk1 = images(GhostW1ID).copy
  val walk2 = images(GhostW2ID).copy
  val imgs = Array[Drawable](walk1, walk2)
  var currImage = walk1
}

object Elsa extends EnemyType {
  val id = ElsaID
  val maxHp = 15
  val attack = 4
  val defense = 3 
  val speed = 2
}

class Elsa(xc: Float, yc: Float) extends Enemy(xc, yc, Elsa) {
  val walk1 = images(ElsaID).copy
  val imgs = Array[Drawable](walk1)
  var currImage = walk1
}

object PowerRanger extends EnemyType {
  val id = PowerRangerW1ID
  val maxHp = 20
  val attack = 10
  val defense = 6 
  val speed = 4
}

class PowerRanger(xc: Float, yc: Float) extends Enemy(xc, yc, PowerRanger) {
  val walk1 = images(PowerRangerW1ID).copy
  val walk2 = images(PowerRangerW2ID).copy
  val imgs = Array[Drawable](walk1, walk2)
  var currImage = walk1
}
