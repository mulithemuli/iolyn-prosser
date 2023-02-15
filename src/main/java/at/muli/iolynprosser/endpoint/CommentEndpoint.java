package at.muli.iolynprosser.endpoint;

import at.muli.iolynprosser.dto.Comment;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin
@RestController
@RequestMapping("/api/comment")
public class CommentEndpoint {

    private final Map<String, Comment> comments = new ConcurrentHashMap<>();

    @GetMapping("")
    public ResponseEntity<List<Comment>> listAllComments() {
        return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(new ArrayList<>(comments.values()));
    }

    @PutMapping("")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        if (comments.values().stream().anyMatch(storedComment -> storedComment.comment().equals(comment.comment()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "comment already present");
        }
        Comment newComment = new Comment(UUID.randomUUID().toString(), comment.comment());
        comments.put(newComment.id(), newComment);
        return ResponseEntity.ok(newComment);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Comment> deleteComment(@PathVariable("id") String id) {
        return ResponseEntity.ok(comments.remove(id));
    }

    @PostMapping("{id}")
    public ResponseEntity<Comment> modifyComment(@PathVariable("id") String id, @RequestBody Comment comment) {
        if (!comments.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "comment with id not found");
        }
        if (comments.get(id).comment().equals(comment.comment())) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }
        return ResponseEntity.ok(comments.put(id, comment));
    }
}
