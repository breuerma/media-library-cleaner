package com.traumkern.mediaregistry.frontend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.traumkern.mediaregistry.frontend.model.FileRemovalReport;
import com.traumkern.mediaregistry.frontend.model.FileRemovalResult;
import com.traumkern.mediaregistry.service.FileRemovalFailedException;
import com.traumkern.mediaregistry.service.MediaRegistryService;
import com.traumkern.mediaregistry.service.model.Duplicates;
import com.traumkern.mediaregistry.service.model.PathAndSize;

@Controller
public class MediaRegistryController {

    @Autowired
    private MediaRegistryService service;

    @RequestMapping("/")
    public String indexPage(final Model argModel) {
        final long myNumberOfMediaFiles = this.service.countAllRegisteredFiles();
        argModel.addAttribute("numberOfMediaFiles", myNumberOfMediaFiles);
        final List<Duplicates> myDuplicatesList = this.service.fetchAllDuplicates();
        argModel.addAttribute("duplicatesList", myDuplicatesList);
        argModel.addAttribute("duplicatesListSize", myDuplicatesList.size());
        return "duplicate_list";
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam(name = "remove", defaultValue = "") final long[] argIdToBeRemovedArray, final Model argModel) {
        final List<FileRemovalResult> myRemovedFileResultList = new ArrayList<>();
        for (final long myIdToBeRemoved : argIdToBeRemovedArray) {
            try {
                final PathAndSize myRemovedFilePathAndSize = this.service.removeFile(myIdToBeRemoved);
                myRemovedFileResultList.add(FileRemovalResult.success(myRemovedFilePathAndSize));
            } catch (final FileRemovalFailedException frfe) {
                myRemovedFileResultList.add(FileRemovalResult.failure(frfe.getFilePath(), frfe.getMessage()));
            }
        }
        final FileRemovalReport myReport = FileRemovalReport.fromResultList(myRemovedFileResultList);
        argModel.addAttribute("report", myReport);
        return "removal_confirmation";
    }

}
