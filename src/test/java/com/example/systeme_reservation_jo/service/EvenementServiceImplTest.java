package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvenementServiceImplTest {

    @Mock
    private EvenementRepository repo;

    @InjectMocks
    private EvenementServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEvenements_ReturnsList() {
        Evenement e1 = new Evenement(
                1L,
                "E1",
                "D1",
                LocalDateTime.now().plusDays(1),
                "L1",
                100,
                100,
                "cat",
                BigDecimal.valueOf(25.5),
                true
        );
        Evenement e2 = new Evenement(
                2L,
                "E2",
                "D2",
                LocalDateTime.now().plusDays(2),
                "L2",
                50,
                50,
                "cat",
                BigDecimal.valueOf(30.0),
                true
        );
        List<Evenement> evts = Arrays.asList(e1, e2);
        when(repo.findAll()).thenReturn(evts);
        List<Evenement> result = service.getAllEvenements();
        assertEquals(2, result.size());
        assertEquals(evts, result);
        verify(repo, times(1)).findAll();
    }

    @Test
    void getEvenementById_ExistingId_ReturnsEvenement() {
        Long id = 1L;
        Evenement evt = new Evenement(
                id,
                "E1",
                "D1",
                LocalDateTime.now().plusDays(1),
                "L1",
                100,
                100,
                "cat",
                BigDecimal.valueOf(25.5),
                true
        );
        when(repo.findById(id)).thenReturn(Optional.of(evt));
        Optional<Evenement> result = service.getEvenementById(id);
        assertTrue(result.isPresent());
        assertEquals(evt, result.get());
        verify(repo, times(1)).findById(id);
    }

    @Test
    void getEvenementById_NonExistingId_ReturnsEmptyOptional() {
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());
        Optional<Evenement> result = service.getEvenementById(id);
        assertFalse(result.isPresent());
        verify(repo, times(1)).findById(id);
    }

}
