package com.example.demo.controller;

import com.example.demo.domain.dto.ChatDto;
import com.example.demo.domain.dto.ChatRoom;
import com.example.demo.domain.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ChatRoom createRoom(@RequestParam String name) {
        return chatService.createRoom(name);
    }

    @DeleteMapping
    public void deleteRoom(@RequestParam String name) {
        chatService.deleteRoom(name);
    }

    @GetMapping
    public Result findAllRoom() {
        List<ChatDto> allRoom = chatService.findAllRoom();
        return new Result(allRoom, allRoom.size());
    }


}