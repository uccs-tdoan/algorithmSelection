# convert arff to csv
# Tri doan
# Date: May 2015

library(foreign)

# change to directory that contains arff data
setwd("C:/Dataset/data/test")
# get file names

files <- list.files(pattern = "\\.arff$")
n <- length(files)

for( i in  1:n) { 
  df <- read.arff(files[i])
  nameFile <- strsplit(files[i], "\\.")[[1]][1]
  print(nameFile)
  write.table(df,paste0("newdata/",nameFile,".csv"),sep=",", col.names=TRUE, row.names=FALSE)
}

