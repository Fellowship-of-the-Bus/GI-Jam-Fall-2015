package com.github.fellowship_of_the_bus
package eshe
package game
import IDMap._
import lib.game.GameConfig.{Width,Height}
import lib.ui.{Drawable}
import state.ui.GameArea

import eshe.state.ui.PlayerListener

trait CharacterType {
  def id: Int
  def maxHp: Int
  def attack: Int
  def defense: Int
  def speed: Int
}

abstract class Character(xc: Float, yc: Float, val base: CharacterType) extends GameObject(xc, yc) {
  def name: String

  var hp: Float = base.maxHp
  def maxHp = base.maxHp
  def attack = base.attack
  def defense = base.defense
  def speed = base.speed

  def id: Int = base.id

  def imgs: Array[Drawable]
  var currImage: Drawable
  val numSteps = 20
  var steps = numSteps
  var index = 0

  def hit(c: Character) = {
    val damage = (attack - c.defense)
    c.hp = c.hp - damage

    if (c.hp <= 0) {
      c.inactivate
      c match {
        case e: Enemy => notify(x => x.enemyDied(e))
        case p: Player => notify(x => x.playerDied(p))
      }
    }
  }

  var listeners = List[PlayerListener]()
  def addListener(l: PlayerListener) = {
    listeners = l::listeners
  }

  def notify(event: (PlayerListener) => Unit) = {
    for (l <- listeners) {
      event(l)
    }
  }
  
  def getTargets(y:Float, range: Float, enemy: Boolean, game: Game) = {
    val tolerance: Float = 20.0f
    var targets: List[Character] = null
    if (enemy) targets = game.players.toList
    else targets = game.enemies
    for (t <- targets; if (t.active)) {
      if ((y+tolerance <= t.y+t.height) && (y-tolerance >= t.y) && (x + width + range >= t.x) && (x + width + range <= t.x + t.width)) {
        hit(t)
      }
    }
  }

  override def move(xamt: Float, yamt: Float): Unit = {
    if (x < 0)  {
      x = 0
    }
    if (x > (Width - width)) {
      x = Width - width
    }
    x = x + xamt
    if (y < 0) {
       y = 0
    }
    if (y > (GameArea.height - height)) {
       y = GameArea.height - height
    }
    y = y + yamt
    if ((xamt != 0) || (yamt != 0)) {
      steps = Math.max(0, steps-1)
      if (steps == 0) {
        steps = numSteps
        index = (index + 1) % imgs.length
        img = imgs(index)
      }
    }
  }

}
