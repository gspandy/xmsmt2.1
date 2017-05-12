package net.wit.service;

import java.util.List;

import net.wit.entity.Note;
import net.wit.entity.NoteReply;

public interface NoteReplyService extends BaseService<NoteReply, Long> {

	List<NoteReply> findListByDate(Note note);
	
	void saveNoteReply(NoteReply noteReply);
}
