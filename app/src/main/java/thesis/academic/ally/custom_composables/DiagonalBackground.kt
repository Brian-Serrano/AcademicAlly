package thesis.academic.ally.custom_composables

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection


class DiagonalBackground : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            moveTo(0f, size.height / 2 + 100)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            lineTo(size.width, size.height / 2)
            close()
        })
    }
}