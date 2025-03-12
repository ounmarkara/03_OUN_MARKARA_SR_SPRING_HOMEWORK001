package org.example.spring_homework001.controller;

import org.example.spring_homework001.model.entity.Ticket;
import org.example.spring_homework001.model.request.BulkUpdatePaymentStatus;
import org.example.spring_homework001.model.request.TicketRequest;
import org.example.spring_homework001.model.request.TicketStatus;
import org.example.spring_homework001.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {
    private final static List<Ticket> TICKETS = Collections.synchronizedList(new ArrayList<>());
    private final static AtomicLong ATOMIC_LONG = new AtomicLong(5L);

    public TicketController() {
        TICKETS.add(new Ticket(1L, "Koko", LocalDate.of(2024, 2, 11), "PP", "SR", 1200.0, false, TicketStatus.CANCELLED, 8));
        TICKETS.add(new Ticket(2L, "Koko", LocalDate.of(2023, 4, 21), "PP", "SR", 1200.0, true, TicketStatus.CANCELLED, 9));
        TICKETS.add(new Ticket(3L, "Koko", LocalDate.of(2020, 1, 20), "PP", "SR", 1200.0, true, TicketStatus.BOOKED, 4));
        TICKETS.add(new Ticket(4L, "Koko", LocalDate.of(2016, 8, 21), "PP", "SR", 1200.0, true, TicketStatus.BOOKED, 2));
    }

    @GetMapping
    public ResponseEntity<Response<List<Ticket>>> getAllTickets(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "3") int size) {
        ArrayList<Ticket> shownTickets = new ArrayList<>();
        int startCount = (page - 1) * size;
        for (int i = startCount; i < startCount + size; i++) {
            if (i < TICKETS.size())
                shownTickets.add(TICKETS.get(i));
        }
        return ResponseEntity.ok(new Response<>(true, "Tickets retrieved successfully", HttpStatus.OK, shownTickets));
    }

    @PostMapping
    public ResponseEntity<Response<Ticket>> createTicket(@RequestBody TicketRequest request) {
        Ticket ticket = new Ticket(
                ATOMIC_LONG.getAndIncrement(),
                request.getPassengerName(),
                request.getTravelDate(),
                request.getSourceStation(),
                request.getDestinationStation(),
                request.getPrice(),
                request.isPaymentStatus(),
                request.getTicketStatus(),
                request.getSeatNumber()
        );
        TICKETS.add(ticket);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>(true, "Ticket created successfully", HttpStatus.CREATED, ticket));
    }

    @GetMapping("/{ticket-id}")
    public ResponseEntity<Response<Ticket>> getTicketById(@PathVariable("ticket-id") Long ticketId) {
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                return ResponseEntity.ok(new Response<>(true, "Ticket found", HttpStatus.OK, ticket));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response<>(false, "Ticket not found", HttpStatus.NOT_FOUND, null));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<List<Ticket>>> searchTicketByName(@RequestParam String name) {
        List<Ticket> tickets = new ArrayList<>();
        for (Ticket ticket : TICKETS) {
            if (ticket.getPassengerName().toLowerCase().contains(name.toLowerCase())) {
                tickets.add(ticket);
            }
        }
        return ResponseEntity.ok(new Response<>(true, "Tickets found", HttpStatus.OK, tickets));
    }

    @GetMapping("/filter")
    public ResponseEntity<Response<List<Ticket>>> filterTickets(
            @RequestParam(required = false) TicketStatus ticketStatus,
            @RequestParam(required = false) LocalDate travelDate) {
        List<Ticket> filteredTickets = new ArrayList<>();
        for (Ticket ticket : TICKETS) {
            boolean matchesStatus = (ticketStatus == null || ticket.getTicketStatus() == ticketStatus);
            boolean matchesDate = (travelDate == null || ticket.getTravelDate().equals(travelDate));
            if (matchesDate && matchesStatus) {
                filteredTickets.add(ticket);
            }
        }
        return ResponseEntity.ok(new Response<>(true, "Tickets filtered successfully", HttpStatus.OK, filteredTickets));
    }

    @PutMapping("/{ticket-id}")
    public ResponseEntity<Response<Ticket>> updateTicketById(
            @PathVariable("ticket-id") Long ticketId,
            @RequestBody TicketRequest request) {
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                ticket.setPassengerName(request.getPassengerName());
                ticket.setTravelDate(request.getTravelDate());
                ticket.setSourceStation(request.getSourceStation());
                ticket.setDestinationStation(request.getDestinationStation());
                ticket.setPrice(request.getPrice());
                ticket.setPaymentStatus(request.isPaymentStatus());
                ticket.setTicketStatus(request.getTicketStatus());
                ticket.setSeatNumber(request.getSeatNumber());
                return ResponseEntity.ok(new Response<>(true, "Ticket updated successfully", HttpStatus.OK, ticket));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response<>(false, "Ticket not found", HttpStatus.NOT_FOUND, null));
    }

    @DeleteMapping("/{ticket-id}")
    public ResponseEntity<Response<String>> deleteTicketById(@PathVariable("ticket-id") Long ticketId) {
        for (Ticket ticket : TICKETS) {
            if (ticket.getTicketId().equals(ticketId)) {
                TICKETS.remove(ticket);
                return ResponseEntity.ok(new Response<>(true, "Ticket deleted successfully", HttpStatus.OK, "Deleted ticket ID: " + ticketId));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response<>(false, "Ticket not found", HttpStatus.NOT_FOUND, null));
    }

    @PutMapping("/bulk-update-payment")
    public ResponseEntity<Response<List<Ticket>>> bulkUpdatePaymentStatus(
            @RequestBody BulkUpdatePaymentStatus request) {
        if (request == null || request.getTicketIds() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(false, "Invalid request: ticketIds cannot be null", HttpStatus.BAD_REQUEST, null));
        }

        List<Ticket> updatedTickets = new ArrayList<>();
        Integer[] ticketIds = request.getTicketIds();
        boolean newPaymentStatus = request.isPaymentStatus();

        if (ticketIds.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(false, "No ticket IDs provided", HttpStatus.BAD_REQUEST, null));
        }

        for (Integer ticketId : ticketIds) {
            for (Ticket ticket : TICKETS) {
                if (ticket.getTicketId().equals(ticketId.longValue())) {
                    ticket.setPaymentStatus(newPaymentStatus);
                    updatedTickets.add(ticket);
                    break;
                }
            }
        }

        if (updatedTickets.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>(false, "No tickets found to update", HttpStatus.NOT_FOUND, null));
        }
        return ResponseEntity.ok(new Response<>(true, "Payment status updated successfully", HttpStatus.OK, updatedTickets));
    }
}