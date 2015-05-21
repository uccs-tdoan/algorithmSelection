# R script to compute statistical summaries for datasets
# Tri Doan
# Sept 30, 2014
library(foreign)
library("infotheo",lib="c:/r")
library("moments",lib="c:/r")
library("psych",lib="c:/r")
#setwd("c:/dataset/metalearning")

normalized <- function (m) {if (m !=0) m/sqrt(sum(m*m)) else 0}

# get file names


# createMeta function
createMeta <- function (files) {
  n <- length(files)
  nInstance <- numeric()
  ClassInst <-  numeric()
  AttrClass <- numeric()
  median1 <- numeric()
  mean1 <- numeric()
  sdt1 <- numeric()
  skewness1 <- numeric()
  mad1 <- numeric()
  
  median2 <- numeric()
  mean2 <- numeric()
  sdt2 <- numeric()
  skewness2 <- numeric()
  mad2 <- numeric()
  
  median3 <- numeric()
  mean3 <- numeric()
  sdt3 <- numeric()
  skewness3 <- numeric()
  mad3 <- numeric()
  
  median4 <- numeric()
  mean4 <- numeric()
  sdt4 <- numeric()
  skewness4 <- numeric()
  mad4 <- numeric()
  
  entro_Class <- numeric() # entropy of target class
  totalCorr <- numeric()  # known as mulitinformation
  
  dataset <- character()
  for( i in  1:n) { 
    df <- read.arff(files[i])
    
        dataset[i] <- strsplit(files[i],'[.]')[[1]][1]
        ClassInst[i] <- length(unique(df$class))/nrow(df)
        AttrClass[i] <- 4/ length(unique(df$class))
        median1[i] <- median(df[,1],na.rm=TRUE)
        mean1[i] <- mean(df[,1],na.rm=TRUE)
        sdt1[i] <-  sd(df[,1],na.rm=TRUE)
        skewness1[i] <- skewness(df[,1],na.rm=TRUE)
        mad1[i] <- mad(df[,1])   # median absolute deviation
    
    median2[i] <- median(df[,2],na.rm=TRUE)
    mean2[i] <- mean(df[,2],na.rm=TRUE)
    sdt2[i] <- sd(df[,2],na.rm=TRUE)
    #m_coeffVar2[i] <- sd(df[,2],na.rm=TRUE)/mean(df[,2],na.rm=TRUE)
    #m_indexDisper2[i] <- var(df[,2],na.rm=TRUE)/mean(df[,2],na.rm=TRUE)
    skewness2[i] <- skewness(df[,2],na.rm=TRUE)
    mad2[i] <- mad(df[,2])   # median absolute deviation
    
      median3[i] <- median(df[,3],na.rm=TRUE)
      mean3[i] <- mean(df[,3],na.rm=TRUE)
      sdt3[i] <- sd(df[,3],na.rm=TRUE)
      #m_coeffVar3[i] <- sd(df[,3],na.rm=TRUE)/mean(df[,3],na.rm=TRUE)
      #m_indexDisper3[i] <- var(df[,3],na.rm=TRUE)/mean(df[,3],na.rm=TRUE)
      skewness3[i] <- skewness(df[,3],na.rm=TRUE)
      mad3[i] <- mad(df[,3])   # median absolute deviation
    
    median4[i] <- median(df[,4],na.rm=TRUE)
    mean4[i] <- mean(df[,4],na.rm=TRUE)
    sdt4[i] <- sd(df[,4],na.rm=TRUE)
    #m_coeffVar4[i] <- sd(df[,4],na.rm=TRUE)/mean(df[,4],na.rm=TRUE)
    #m_indexDisper4[i] <- var(df[,4],na.rm=TRUE)/mean(df[,4],na.rm=TRUE)
    skewness4[i] <- skewness(df[,4],na.rm=TRUE)
    mad4[i] <- mad(df[,4])   # median absolute deviation
    
    entro_Class[i] <- entropy(df[ncol(df)],method="sg") # compute entropy for targetmu 
    # compute multiinformation (total correlation) for all attributes
    totalCorr[i] <- multiinformation(discretize(df[,-ncol(df)]),method="sg")
    
    
  }
  dat<-data.frame(dataset,ClassInst,AttrClass,median1,mean1,sdt1,skewness1,mad1,median2,mean2,sdt2,skewness2,mad2,median3,mean3,sdt3,skewness3,mad3,median4,mean4,sdt4,skewness4,mad4,entro_Class,totalCorr)
  
  write.table(dat,file="../TestStats.csv",sep=",",row.names=FALSE,col.names=TRUE)
}

setwd("C:/data/PCA/test") 
files <- list.files(getwd())
n <- length(files)
createMeta(files)

