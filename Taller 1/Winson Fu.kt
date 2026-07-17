
fun main() {

    var nombre = ""
    var apellido = ""
    var edad = 0
    var genero = ""
    var registrado = false

    var opcion = 0
    var tipoPago = ""

    do {
        println("\n===== MENU TRANSPORTE =====")
        println("1. Registrar pasajero")
        println("2. Realizar compra del boleto")
        println("3. Salir")
        print("Seleccione una opción: ")

        try {
            opcion = readln().toInt()

            when (opcion) {

                1 -> {
                    print("Ingrese su Nombre: ")
                    nombre = readln()

                    print("Ingrese su Apellido: ")
                    apellido = readln()

                    print("Ingrese su Edad: ")
                    edad = readln().toInt()

                    do {
                        print("Ingrese su Genero (M o F): ")
                        genero = readln().uppercase()
                    } while (genero != "M" && genero != "F")

                    registrado = true

                    println("Pasajero registrado correctamente.")
                }

                2 -> {

                    if (!registrado) {
                        println("Debe registrar un pasajero primero.")
                        continue
                    }

                    var nombreCompleto = "$nombre $apellido"

                    var precioBase = 20.0
                    var descuento = 0.0

                    if (edad < 12) {
                        descuento = 0.05
                    } else if ((genero == "F" && edad > 57) ||
                        (genero == "M" && edad > 62)) {
                        descuento = 0.15
                    }

                    var montoDescuento = precioBase * descuento
                    var costoFinal = precioBase - montoDescuento

                    do {
                        println("\nTipo de Pago:")
                        println("Visa | Clave | Cheque | Efectivo | Transferencia")
                        print("Ingrese tipo de pago: ")

                        tipoPago = readln().trim().uppercase()

                        if (
                            tipoPago != "VISA" &&
                            tipoPago != "CLAVE" &&
                            tipoPago != "CHEQUE" &&
                            tipoPago != "EFECTIVO" &&
                            tipoPago != "TRANSFERENCIA"
                        ) {
                            println("Tipo de pago inválido.")
                        }

                    } while (
                        tipoPago != "VISA" &&
                        tipoPago != "CLAVE" &&
                        tipoPago != "CHEQUE" &&
                        tipoPago != "EFECTIVO" &&
                        tipoPago != "TRANSFERENCIA"
                    )

                    println("\n--- TRANSPORTE UTP S.A. -----")
                    println("RUC: 01-2531-4507")
                    println("\nTERMINAL PRINCIPAL\n")

                    println("CLIENTE: $nombreCompleto")
                    println("EDAD: $edad")
                    println("GENERO: $genero")
                    println("PAGO: $tipoPago")
                    println("COSTO BASE: B/. %.2f".format(precioBase))
                    println("DESCUENTO: B/. %.2f".format(montoDescuento))
                    println("TOTAL A PAGAR: B/. %.2f".format(costoFinal))

                    println("\nBUEN VIAJE!")
                }

                3 -> println("Saliendo del sistema...")

                else -> println("Opción inválida.")
            }
        } catch (e: Exception) {
            println("Entrada inválida")
        }


    } while (opcion != 3)
}