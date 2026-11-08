package com.famille.gestioncompte.controller;

import com.famille.gestioncompte.dto.MonthData;
import com.famille.gestioncompte.dto.OperationRequest;
import com.famille.gestioncompte.dto.OperationResponse;
import com.famille.gestioncompte.dto.PointerRequest;
import com.famille.gestioncompte.dto.UpdateOperationRequest;
import com.famille.gestioncompte.service.GoogleSheetsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/months")
public class MonthController {

    private final GoogleSheetsService sheetsService;

    public MonthController(GoogleSheetsService sheetsService) {
        this.sheetsService = sheetsService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getMonths() throws IOException {
        return ResponseEntity.ok(sheetsService.getMonthKeys());
    }

    @GetMapping("/{monthKey}")
    public ResponseEntity<MonthData> getMonth(@PathVariable @Pattern(regexp = "[A-Za-z]{2,12}[0-9]{4}") String monthKey) throws IOException {
        return ResponseEntity.ok(sheetsService.getMonthData(monthKey));
    }

    @PostMapping("/{monthKey}/operations")
    public ResponseEntity<OperationResponse> addOperation(
            @PathVariable @Pattern(regexp = "[A-Za-z]{2,12}[0-9]{4}") String monthKey,
            @Valid @RequestBody OperationRequest request) throws IOException {

        boolean created = sheetsService.addOperation(monthKey, request);
        String message = created ? "Mois de " + monthKey + " créé" : "Opération enregistrée";
        return ResponseEntity.ok(new OperationResponse(created, message));
    }

    @PatchMapping("/{monthKey}/operations/{ligne}")
    public ResponseEntity<Void> updateOperation(
            @PathVariable @Pattern(regexp = "[A-Za-z]{2,12}[0-9]{4}") String monthKey,
            @PathVariable @Min(1) int ligne,
            @Valid @RequestBody UpdateOperationRequest request) throws IOException {
        sheetsService.updateOperation(monthKey, ligne, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{monthKey}/operations/{ligne}")
    public ResponseEntity<Void> deleteOperation(
            @PathVariable @Pattern(regexp = "[A-Za-z]{2,12}[0-9]{4}") String monthKey,
            @PathVariable @Min(1) int ligne) throws IOException {
        sheetsService.deleteOperation(monthKey, ligne);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{monthKey}/depointer-reel/{ligne}")
    public ResponseEntity<Void> depointeOperation(
            @PathVariable @Pattern(regexp = "[A-Za-z]{2,12}[0-9]{4}") String monthKey,
            @PathVariable @Min(1) int ligne) throws IOException {
        sheetsService.depointeOperation(monthKey, ligne);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{monthKey}/operations/{ligne}/transferer")
    public ResponseEntity<Map<String, String>> transfererOperation(
            @PathVariable @Pattern(regexp = "[A-Za-z]{2,12}[0-9]{4}") String monthKey,
            @PathVariable @Min(1) int ligne) throws IOException {
        String targetMonth = sheetsService.transfererOperation(monthKey, ligne);
        return ResponseEntity.ok(Map.of("targetMonth", targetMonth));
    }

    @PostMapping("/{monthKey}/pointer/{ligne}")
    public ResponseEntity<Void> pointerOperation(
            @PathVariable @Pattern(regexp = "[A-Za-z]{2,12}[0-9]{4}") String monthKey,
            @PathVariable @Min(1) int ligne,
            @Valid @RequestBody(required = false) PointerRequest body) throws IOException {

        String date               = body != null ? body.date()               : null;
        String libelle            = body != null ? body.libelle()             : null;
        Double montant            = body != null ? body.montantAsDouble()     : null;
        boolean deduireDesCourses = body != null && body.deduireDesCourses();
        sheetsService.pointerOperation(monthKey, ligne, date, libelle, montant, deduireDesCourses);
        return ResponseEntity.ok().build();
    }
}
