package com.seq.sisas.utilidad; // Paquete de utilidades.

import com.google.zxing.BarcodeFormat; // Enumeración de formatos (QR_CODE, CODE_128, etc).
import com.google.zxing.client.j2se.MatrixToImageWriter; // Convierte la matriz de bits en imagen PNG.
import com.google.zxing.common.BitMatrix; // Matriz de puntos negros y blancos del QR.
import com.google.zxing.qrcode.QRCodeWriter; // Motor que calcula la matriz del QR.
import java.io.File; // Manejo de archivos y carpetas.
import java.nio.file.Path; // Rutas de archivo modernas de Java.
import java.nio.file.Paths; // Utilidad para construir Paths.

/**
 * GeneradorQR: crea la imagen física del código QR en el disco duro.
 * CONEXIONES:
 * - Invocada por com.seq.sisas.servicio.ServicioEstudiante.
 * - Usa librerías externas de com.google.zxing.
 */
public class GeneradorQR { // Clase utilitaria.

    // Genera el archivo PNG en la carpeta datos/qr/.
    public static void generarImagen(String codigo, String rutaCarpeta) throws Exception { // Lanza Exception si falla I/O o ZXing.
        
        File carpeta = new File(rutaCarpeta); // Objeto File apuntando a la carpeta destino.
        if (!carpeta.exists()) { // Si la carpeta no existe.
            carpeta.mkdirs(); // La crea (incluyendo subcarpetas si faltan).
        } // Fin de validación de carpeta.

        QRCodeWriter qrWriter = new QRCodeWriter(); // Instancia el motor de cálculo de QR.
        // Calcula la matriz de 200x200 píxeles basada en el texto del código.
        BitMatrix matrix = qrWriter.encode(codigo, BarcodeFormat.QR_CODE, 200, 200); 

        Path path = Paths.get(rutaCarpeta + "/" + codigo + ".png"); // Construye la ruta final del archivo (ej. datos/qr/ABCD1234.png).
        MatrixToImageWriter.writeToPath(matrix, "PNG", path); // Escribe la matriz en el disco como imagen PNG.
    }
}