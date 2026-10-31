package com.daa.guerradeplatillos.model

import androidx.annotation.DrawableRes
import com.daa.guerradeplatillos.R

/**
 * Catálogo de imágenes de avatar (res/drawable-nodpi/avatar_*.jpg).
 *
 * TODO: la selección de imagen de avatar todavía no está implementada.
 *  Cuando se implemente, guardar el `id` elegido en [com.daa.guerradeplatillos.data.UserRepository]
 *  y mostrarlo con `painterResource(AvatarCatalog.byId(id).drawable)`.
 */
object AvatarCatalog {

    data class AvatarImage(val id: Int, @param:DrawableRes val drawable: Int)

    val all: List<AvatarImage> = listOf(
        AvatarImage(1, R.drawable.avatar_1),
        AvatarImage(2, R.drawable.avatar_2),
        AvatarImage(3, R.drawable.avatar_3),
        AvatarImage(4, R.drawable.avatar_4),
        AvatarImage(5, R.drawable.avatar_5),
        AvatarImage(6, R.drawable.avatar_6),
        AvatarImage(7, R.drawable.avatar_7),
    )

    fun byId(id: Int): AvatarImage? = all.firstOrNull { it.id == id }
}
