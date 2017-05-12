package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.NoteReplyDao;
import net.wit.entity.Note;
import net.wit.entity.NoteReply;
import net.wit.service.NoteReplyService;

import org.springframework.stereotype.Service;

@Service("noteReplyServiceImpl")
public class NoteReplyServiceImpl extends BaseServiceImpl<NoteReply, Long> implements NoteReplyService {

	@Resource(name = "noteReplyDaoImpl")
	private NoteReplyDao noteReplyDao;

	public List<NoteReply> findListByDate(Note note) {
		return noteReplyDao.findListByDate(note);
	}

	public void saveNoteReply(NoteReply noteReply) {
		noteReplyDao.persist(noteReply);
	}
	
}
