package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus

@Composable
fun DeliveryMapVisualizer(
    order: Order,
    onCallDriver: () -> Unit,
    onChatDriver: () -> Unit,
    onAdvanceStep: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mapRadar")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("delivery_map_screen")
    ) {
        // Map Canvas Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .background(Color(0xFFE5E7EB))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw background land / grid
                drawRect(color = Color(0xFFF1F5F9))

                // Draw green parks
                drawRoundRect(
                    color = Color(0xFFD1FAE5),
                    topLeft = Offset(w * 0.08f, h * 0.12f),
                    size = androidx.compose.ui.geometry.Size(w * 0.28f, h * 0.25f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                drawRoundRect(
                    color = Color(0xFFD1FAE5),
                    topLeft = Offset(w * 0.65f, h * 0.55f),
                    size = androidx.compose.ui.geometry.Size(w * 0.26f, h * 0.3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )

                // Secondary roads grid
                val gridPaintColor = Color(0xFFE2E8F0)
                for (i in 1..8) {
                    val y = h * (i / 9f)
                    drawLine(
                        color = gridPaintColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 6f
                    )
                }
                for (j in 1..6) {
                    val x = w * (j / 7f)
                    drawLine(
                        color = gridPaintColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 6f
                    )
                }

                // Main Arterial Highway (Bole Road simulation)
                val highwayPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.85f)
                    cubicTo(
                        w * 0.35f, h * 0.65f,
                        w * 0.55f, h * 0.50f,
                        w * 0.85f, h * 0.18f
                    )
                }
                // Highway casing & surface
                drawPath(
                    path = highwayPath,
                    color = Color(0xFFCBD5E1),
                    style = Stroke(width = 24f, cap = StrokeCap.Round)
                )
                drawPath(
                    path = highwayPath,
                    color = Color(0xFFFFFFFF),
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )

                // Live GPS Route Line
                val storePoint = Offset(w * 0.22f, h * 0.78f)
                val driverProgress = when (order.status) {
                    OrderStatus.CONFIRMED -> 0.05f
                    OrderStatus.PACKED -> 0.25f
                    OrderStatus.IN_TRANSIT -> 0.65f
                    OrderStatus.DELIVERED -> 1.0f
                }
                val customerPoint = Offset(w * 0.82f, h * 0.22f)
                val driverPoint = Offset(
                    storePoint.x + (customerPoint.x - storePoint.x) * driverProgress,
                    storePoint.y + (customerPoint.y - storePoint.y) * driverProgress
                )

                // Route dashed background
                val routePath = Path().apply {
                    moveTo(storePoint.x, storePoint.y)
                    quadraticBezierTo(w * 0.45f, h * 0.6f, customerPoint.x, customerPoint.y)
                }
                drawPath(
                    path = routePath,
                    color = Color(0xFF0072CE).copy(alpha = 0.35f),
                    style = Stroke(
                        width = 10f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f))
                    )
                )

                // Active completed portion of route
                val completedPath = Path().apply {
                    moveTo(storePoint.x, storePoint.y)
                    quadraticBezierTo(
                        storePoint.x + (w * 0.45f - storePoint.x) * driverProgress,
                        storePoint.y + (h * 0.6f - storePoint.y) * driverProgress,
                        driverPoint.x,
                        driverPoint.y
                    )
                }
                drawPath(
                    path = completedPath,
                    color = Color(0xFF00897B),
                    style = Stroke(width = 12f, cap = StrokeCap.Round)
                )

                // Draw Store Marker (Pickup)
                drawCircle(color = Color(0xFF1E293B), radius = 22f, center = storePoint)
                drawCircle(color = Color(0xFF0072CE), radius = 16f, center = storePoint)

                // Draw Destination Pin (Customer)
                drawCircle(color = Color(0xFFEF4444).copy(alpha = 0.3f), radius = 28f, center = customerPoint)
                drawCircle(color = Color(0xFFEF4444), radius = 18f, center = customerPoint)
                drawCircle(color = Color.White, radius = 7f, center = customerPoint)

                // Draw Driver Bajaj / Motor Marker with Radar Pulse
                if (order.status != OrderStatus.DELIVERED) {
                    drawCircle(
                        color = Color(0xFF00897B).copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = driverPoint
                    )
                }
                drawCircle(color = Color(0xFF004D40), radius = 24f, center = driverPoint)
                drawCircle(color = Color(0xFF00897B), radius = 18f, center = driverPoint)
                drawCircle(color = Color.White, radius = 6f, center = driverPoint)
            }

            // Top Floating GPS Pill
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.88f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = Color(0xFF34D399),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (order.status == OrderStatus.DELIVERED) "Delivered to Bole Medhanialem" else "Driver in transit • ${order.estimatedMinutes} mins away",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom Delivery Details & Status Card
        Surface(
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Stepper Milestones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OrderStatus.values().forEach { step ->
                        val isDone = step.stepIndex <= order.status.stepIndex
                        val isCurrent = step == order.status

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> Color(0xFF00897B)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${step.stepIndex + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (step) {
                                    OrderStatus.CONFIRMED -> "Confirmed"
                                    OrderStatus.PACKED -> "Packed"
                                    OrderStatus.IN_TRANSIT -> "In Transit"
                                    OrderStatus.DELIVERED -> "Arrived"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Driver Profile & Contact Bar
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00897B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeliveryDining,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = order.driverName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = order.driverVehicle,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Ref: ${order.paymentReference}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Call Driver Button
                        IconButton(
                            onClick = onCallDriver,
                            modifier = Modifier
                                .testTag("call_driver_button")
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Driver",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        // Chat Driver Button
                        IconButton(
                            onClick = onChatDriver,
                            modifier = Modifier
                                .testTag("chat_driver_button")
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0072CE))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat Driver",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress simulator action
                Button(
                    onClick = onAdvanceStep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("simulate_delivery_step_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (order.status == OrderStatus.DELIVERED) "Order Completed" else "Simulate Next Delivery Milestone",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
