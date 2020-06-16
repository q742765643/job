package com.htht.job.executor.dao.downupload;

import com.htht.job.executor.model.downupload.DownResult;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zzj on 2018/1/15.
 */
@Repository
public interface DownResultDao extends BaseDao<DownResult> {
	
	List<DownResult> findByFileNameAndFilePathAndFileSizeAndZt(String fileName, String filePath, long fileSize, String zt);

	@Modifying
	@Query(nativeQuery = true,value = "update htht_cluster_schedule_download_file_info set file_name=:file_name,file_path=:file_path where id=:id")
	void update(@Param("file_name") String file_name, @Param("file_path") String file_path, @Param("id") String id);

	DownResult findById(String id);

	@Query(nativeQuery = true,value ="SELECT DISTINCT file_path FROM htht_cluster_schedule_download_file_info WHERE real_file_name regexp :reg AND data_time>=:startTime AND data_time<=:endTime AND zt='1'")
	List<DownResult> findByFileName(@Param("reg") String reg,@Param("startTime") String startTime,@Param("endTime") String endTime);

	@Query(nativeQuery = true,value ="SELECT DISTINCT file_path FROM htht_cluster_schedule_download_file_info WHERE real_file_name regexp :reg AND data_time=:Time AND zt='1'")
	List<DownResult> findFilePath(@Param("reg") String reg,@Param("Time") String Time);
	
	@Query(nativeQuery = true,value ="SELECT CONCAT(file_path ,'/', file_name) FROM	htht_cluster_schedule_download_file_info "
			+ " WHERE zt = 1  AND file_size > 0 "
			+ " AND format =:format"
			+ " AND data_time >= :startTime"
			+ " AND data_time <= :endTime"
			+ " AND locate(:reg1, file_name) > 0 "
			+ " LIMIT 30")	
	List<String> findFileByFileNameAndTime(@Param("format") String format,@Param("reg1") String reg1,@Param("startTime") String startTime,@Param("endTime") String endTime);
	
	@Query(nativeQuery = true,value ="SELECT CONCAT(file_path ,'/', file_name) FROM	htht_cluster_schedule_download_file_info "
			+ " WHERE zt = 1  AND file_size > 0 "
			+ " AND format =:format"
			+ " AND data_time >= :startTime"
			+ " AND data_time <= :endTime"
			+ " AND locate(:reg1, file_name) > 0 "
			+ " AND locate(:reg2, file_name) > 0 "
			+ " LIMIT 30")	
	List<String> findFileByFileNameAndTime(@Param("format") String format,@Param("reg1") String reg1,@Param("reg2") String reg2,@Param("startTime") String startTime,@Param("endTime") String endTime);
	
	@Query(nativeQuery = true,value ="SELECT CONCAT(file_path ,'/', file_name) FROM	htht_cluster_schedule_download_file_info "
			+ " WHERE zt = 1  AND file_size > 0 "
			+ " AND data_time >= :startTime"
			+ " AND data_time <= :endTime"
			+ " AND locate(:reg1, file_name) > 0 "
			+ " LIMIT 30")	
	List<String> findFileByFileNameAndTime(@Param("reg1") String reg1,@Param("startTime") String startTime,@Param("endTime") String endTime);

}
