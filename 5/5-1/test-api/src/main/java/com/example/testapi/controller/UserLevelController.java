package com.example.testapi.controller;

import com.example.testapi.controller.request.UserLevelSaveRequest;
import com.example.testapi.controller.request.UserLevelUpdateRequest;
import com.example.testapi.controller.response.UserLevelResponse;
import com.example.testapi.service.UserLevelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-level")
@CrossOrigin(origins = "*")
public class UserLevelController {

    private final UserLevelService userLevelService;

    public UserLevelController(UserLevelService userLevelService) {
        this.userLevelService = userLevelService;
    }

    @PostMapping
    public ResponseEntity<UserLevelResponse> create(@RequestBody UserLevelSaveRequest request) {
        return ResponseEntity.ok(UserLevelResponse.from(userLevelService.create(request.name())));
    }

    @GetMapping
    public ResponseEntity<List<UserLevelResponse>> findAll() {
        List<UserLevelResponse> responses = userLevelService.findAll()
                .stream()
                .map(UserLevelResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserLevelResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(UserLevelResponse.from(userLevelService.findById(id)));
    }

    @PutMapping
    public ResponseEntity<UserLevelResponse> update(@RequestBody UserLevelUpdateRequest request) {
        return ResponseEntity.ok(UserLevelResponse.from(userLevelService.update(request.userLevelId(), request.name())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userLevelService.delete(id);
        return ResponseEntity.ok().build();
    }
}
