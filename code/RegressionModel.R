
#The following codes demonstrate Regression models 


library(caret)
library(e1071)
library(ggplot2)
library(MASS)
library(gbm)

# change working directory
setwd()

# read data
df <- read.csv(..)

# Create training, testing
 set.seed(215)
 TrainRow <- createDataPartition(df[, ncol(df)], p = 0.7, list= FALSE)

 trainData <- df[TrainRow,]
 ctrl <- trainControl(method = "repeatedcv", repeats = 5,number=10)

 trainX <- df[TrainRow,1:ncol(df)-1]
 trainY<-df[TrainRow,ncol(df)]
 testX <- df[-TrainRow, 1:ncol(df)-1]
 observed <- df[-TrainRow,ncol(df)]

# linear regression tree



#Non-linear regression with decision Tree includes 
#1. Classification and Regression Treeslib
#2. Bagging CART
#3. Random Forest
#4. Gradient Boosted Machine
#5. Cubist

#Other non-linear regression includes

#1. Partial Least Square (PLS)
#2. Generate Elastic Net model
#3. generate Ridge Regression
#4. generate LARS model
#5. Generate Multivariate Adaptive Regression Splines  (MARS)

library(rpart)
library(party)
library(RWeka)
library(ipred)
library(randomForest)
library(gbm)
library(Cubist)


#1. Classification and Regression Trees
   
fit <- rpart(SAR~., data=trainData, control=rpart.control(minsplit=5))
# summarize the fit
summary(fit)
# make predictions
predCART <- predict(fit, testX)
# summarize accuracy
rmse <- mean((observed - predCART)^2)


#2. Bagging CART
# fit model
fit <- bagging(SAR~., data= trainData, control=rpart.control(minsplit=5))
# summarize the fit
summary(fit)
# make predictions
predBagCART <- predict(fit, testX)
# summarize accuracy
rmse <- mean(( observed- predBagCART)^2)

3. Random Forest
 # fit model
fit <- randomForest(SAR~., data=trainData)
# summarize the fit
summary(fit)
# make predictions
predRF <- predict(fit, testX)
# summarize accuracy
rmse <- mean((observed - predRF)^2)

4. Gradient Boosted Regression
 # fit model
fit <- gbm(SAR~., data=trainData, distribution="gaussian")
# summarize the fit
summary(fit)
# make predictions
predGBoost <- predict(fit,df[-TrainRow, ],n.trees=5)
# summarize accuracy
rmse <- mean(( observed - predGBoost)^2)

```
5. Cubist
 # fit model
fit <- cubist(trainX, trainY)
# summarize the fit
summary(fit)
# make predictions
predCubist <- predict(fit, testX)
# summarize accuracy
rmse <- mean((observed - predCubist)^2)


# Non-linear regression 
1. Generate Partial Least Square (PLS) model
 set.seed(215)
runPLS <- train(x = trainX, y = trainY, method = "pls", preProcess=c("center","scale"),trControl = ctrl,tuneLenght=25)
predPLS <- predict(runPLS, newdata = testX)

RMSE(predPLS,observed)

2. generate Ridge Regression
set.seed(215)
ridgeGrid <- data.frame(.lambda = seq( .1,4, length = 20))
ridgeRegFit <- train(trainX,trainY, method ="ridge",tuneGrid = ridgeGrid, trControl=ctrl, preProc=c("center","scale"))
ridgeRegFit

ridgeModel <- enet(x = as.matrix(trainX), y = trainY,lambda=0.1)
ridgePred <- predict(ridgeModel,newx=as.matrix(testX),s=1,mode="fraction",type="fit")


RMSE(ridgePred$fit,observed)

#3. generate LARS model
 set.seed(215)
library(lars)
# compute MSEs for a range of coefficient penalties as a fraction of 
# final L1 norm on the interval [0,1] using cross validation
cv.res <- cv.lars(as.matrix(trainX),trainY,type="lasso",mode="fraction",plot=FALSE)
# Choose optimal value one standarf deviation away from minimum MSE
opt.frac <- min(cv.res$cv) + sd(cv.res$cv)
opt.frac <-cv.res$index[which(cv.res$cv < opt.frac)[1]]
# Compute LARS fit
lars.fit <- lars(as.matrix(trainX),trainY,type=c("lasso"))
larsPred <- predict.lars(lars.fit,newx=as.matrix(testX),type="fit")
# get last model fit
last.fit <- dim(larsPred[4]$fit)[2]

predLars <- larsPred[4]$fit[,last.fit]

RMSE(predLars,observed)

4. Generate Elastic Net model

set.seed(215)  
library(elasticnet)
enetGrid <- expand.grid(lambda = c(0, .001, .01, .1, 1), fraction = seq(.05, 1, length = 20))
runENet <-  train(x = trainX, y = trainY, method = "enet", preProcess=c("center","scale"),trControl = ctrl,
                  tuneGrid=enetGrid) 
enetModel <- enet(x = as.matrix(trainX), y = trainY, lambda = 0, normalize = TRUE)
predEnet <- predict(enetModel, newx = as.matrix(testX), s = .1, mode = "fraction",    type = "fit")

RMSE(predEnet$fit,observed)
Elastic <- predEnet$fit

result <- cbind(result,Elastic)

Elastic_residual <- predEnet$fit - observed
residuals <- cbind(residuals,Elastic_residual)
rm(Elastic_residual)

# 5. Generate MARS
set.seed(215)
marsGrid <- expand.grid(.degree = 1:2, .nprune = 2:38)
marsTuned <- train(trainX, trainY, method = "earth",tuneGrid = marsGrid,trControl = trainControl(method = "cv"))
predMars <- predict(marsTuned,testX)
RMSE(predMars,observed)

