package com.github.fellowship_of_the_bus
package eshe
package game

object Projectile {
  val Left = -1
  val Right = 1
}

class Projectile(xc: Float, yc: Float, val id: Int, dir: Int) extends GameObject(xc, yc) {
  def width: Int = 20
  def height: Int = 20

  def move() = x = x + dir
}

