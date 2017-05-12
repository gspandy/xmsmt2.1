package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.NoteDao;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Note;
import net.wit.service.NoteService;

import org.springframework.stereotype.Service;

@Service("noteServiceImpl")
public class NoteServiceImpl extends BaseServiceImpl<Note, Long> implements NoteService {

	@Resource(name = "noteDaoImpl")
	private NoteDao noteDao;
	
	public List<Note> findListByDate(DeliveryCenter deliveryCenter, String date){
		return noteDao.findListByDate(deliveryCenter, date);
	}

	public Note findNote(Long noteId) {
		return noteDao.findNote(noteId);
	}
}
