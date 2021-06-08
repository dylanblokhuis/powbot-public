package framework

fun getPerHour(amount: Int, time: Long): Int {
    return (amount * 3600000.0 / time).toInt()
}

fun formatTime(time: Int): String {
    return String.format("%d:%02d:%02d", time / 3600, time % 3600 / 60, time % 60)
}