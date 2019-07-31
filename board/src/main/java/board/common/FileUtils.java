package board.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.board.dto.BoardFileDto;

// 해당 어노테이션을 사용하여 FileUtils 클래스를 스프링의 bean으로 등록
@Component
public class FileUtils {

	public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
		if(ObjectUtils.isEmpty(multipartHttpServletRequest)) {
			return null;
		}
		
		List<BoardFileDto> fileList = new ArrayList<>();
		
		// 파일이 업로드될 폴더 생성 
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
		ZonedDateTime current = ZonedDateTime.now();
		String path = "images/"+current.format(format);
		File file = new File(path);
		if(file.exists() == false) {
			file.mkdirs();
		}
		
		// 파일 태그 찾기 
		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
		
		String newFilename, originalFileExtension, contentType;
		
		while(iterator.hasNext()) {
			List<MultipartFile> list = multipartHttpServletRequest.getFiles(iterator.next());
			for(MultipartFile multipartFile : list) {
				if(multipartFile.isEmpty() == false) {
					// contentType 
					// 확장자 지정 
					contentType = multipartFile.getContentType();
					if(ObjectUtils.isEmpty(contentType)) {
						break;
					}else {
						if(contentType.contains("image/jpeg")) {
							originalFileExtension = ".jpg";
						}else if(contentType.contains("image/png")) {
							originalFileExtension = ".png";
						}else if(contentType.contains("image/gif")) {
							originalFileExtension = ".gif";
						}else {
							break;
						}
					}
					
					// filename 생성 
					// 파일이 업로드된 나노초를 이용해서 새로운 파일 이름 지정 
					newFilename = Long.toString(System.nanoTime()) + originalFileExtension;
					
					// BoardFileDto에 파일 정보 저장 
					BoardFileDto boardFile = new BoardFileDto();
					boardFile.setBoardIdx(boardIdx);
					boardFile.setFileSize(multipartFile.getSize());
					boardFile.setOriginalFileName(multipartFile.getOriginalFilename());
					boardFile.setStoredFilePath(path + "/" + newFilename);
					
					fileList.add(boardFile);
					
					// 업로드된 파일을 새로운 이름으로 바꾸어 지정된 경로에 저장 
					file = new File(path+"/"+newFilename);
					multipartFile.transferTo(file);
				}
			}
		}
		return fileList;
	}
}
