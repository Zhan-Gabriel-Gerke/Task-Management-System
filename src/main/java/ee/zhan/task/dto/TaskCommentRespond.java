package ee.zhan.task.dto;

public record TaskCommentRespond(Long id, Long taskId, String text, String authorEmail) {
}
