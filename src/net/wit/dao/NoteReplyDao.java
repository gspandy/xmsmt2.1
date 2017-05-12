package net.wit.dao;

import java.util.List;

import net.wit.entity.Note;
import net.wit.entity.NoteReply;

public interface NoteReplyDao extends BaseDao<NoteReply, Long> {

	List<NoteReply> findListByDate(Note note);
}
