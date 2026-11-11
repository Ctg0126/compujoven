package com.compujoven.compujoven.service;

import com.compujoven.compujoven.model.HojaDeVida;
import com.compujoven.compujoven.repository.HojaDeVidaRepository;
import com.compujoven.compujoven.repository.HojaDeVidaResumen;
import com.compujoven.compujoven.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class HojaDeVidaService {

    public static final long TAMANO_MAXIMO = 5L * 1024 * 1024; // 5 MB
    public static final String TIPO_PDF = "application/pdf";
    public static final String TIPO_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final HojaDeVidaRepository hojaDeVidaRepository;
    private final UsuarioRepository usuarioRepository;

    public HojaDeVidaService(HojaDeVidaRepository hojaDeVidaRepository, UsuarioRepository usuarioRepository) {
        this.hojaDeVidaRepository = hojaDeVidaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Guarda (o reemplaza) la hoja de vida del usuario.
     * @throws IllegalArgumentException con un mensaje listo para mostrar si el archivo no es válido
     */
    @Transactional
    public void guardar(Long usuarioId, MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Selecciona un archivo para subir.");
        }
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El archivo supera el tamaño máximo de 5 MB.");
        }

        String nombre = limpiarNombre(archivo.getOriginalFilename());
        if (nombre.isEmpty() || nombre.length() > 150) {
            throw new IllegalArgumentException("El nombre del archivo no es válido (máximo 150 caracteres).");
        }

        String minusculas = nombre.toLowerCase(Locale.ROOT);
        String tipo;
        if (minusculas.endsWith(".pdf")) {
            tipo = TIPO_PDF;
        } else if (minusculas.endsWith(".docx")) {
            tipo = TIPO_DOCX;
        } else {
            throw new IllegalArgumentException("Solo se aceptan archivos en formato .pdf o .docx.");
        }

        byte[] datos = archivo.getBytes();
        if (!contenidoValido(tipo, datos)) {
            throw new IllegalArgumentException("El contenido del archivo no corresponde a un " +
                    (tipo.equals(TIPO_PDF) ? "PDF" : "DOCX") + " válido.");
        }

        HojaDeVida hoja = hojaDeVidaRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            HojaDeVida nueva = new HojaDeVida();
            nueva.setUsuario(usuarioRepository.getReferenceById(usuarioId));
            return nueva;
        });
        hoja.setNombreArchivo(nombre);
        hoja.setTipoContenido(tipo);
        hoja.setTamano(datos.length);
        hoja.setDatos(datos);
        hoja.setFechaCarga(LocalDateTime.now());
        hojaDeVidaRepository.save(hoja);
    }

    @Transactional(readOnly = true)
    public Optional<HojaDeVida> buscar(Long usuarioId) {
        return hojaDeVidaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public Optional<HojaDeVidaResumen> buscarResumen(Long usuarioId) {
        return hojaDeVidaRepository.findResumenByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public Set<Long> idsUsuariosConHojaDeVida() {
        return hojaDeVidaRepository.idsUsuariosConHojaDeVida();
    }

    @Transactional
    public void eliminar(Long usuarioId) {
        hojaDeVidaRepository.eliminarPorUsuarioId(usuarioId);
    }

    // ---------- validaciones ----------

    private String limpiarNombre(String original) {
        if (original == null) {
            return "";
        }
        String n = original.replace("\\", "/");
        n = n.substring(n.lastIndexOf('/') + 1);
        return n.replaceAll("\\p{Cntrl}", "").trim();
    }

    /** No se confía en la extensión ni en el tipo que envía el navegador: se revisa el contenido. */
    private boolean contenidoValido(String tipo, byte[] d) {
        if (tipo.equals(TIPO_PDF)) {
            int limite = Math.min(d.length, 1024);
            String inicio = new String(d, 0, limite, StandardCharsets.ISO_8859_1);
            return inicio.contains("%PDF");
        }
        // DOCX = ZIP (firma PK 03 04) que contiene [Content_Types].xml y la carpeta word/
        if (d.length < 4 || d[0] != 'P' || d[1] != 'K' || d[2] != 3 || d[3] != 4) {
            return false;
        }
        String texto = new String(d, StandardCharsets.ISO_8859_1);
        return texto.contains("[Content_Types].xml") && texto.contains("word/");
    }
}
