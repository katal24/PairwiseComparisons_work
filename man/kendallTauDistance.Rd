% Generated by roxygen2: do not edit by hand
% Please edit documentation in R/pairwiseComparisons.R
\name{kendallTauDistance}
\alias{kendallTauDistance}
\title{Kendall Tau distance for two vectors}
\usage{
kendallTauDistance(list1, list2)
}
\arguments{
\item{list1}{- first rank to compare}

\item{list2}{- second rank to compare}
}
\value{
number of swap which need to be make to order of elements will be identical
}
\description{
Computes Kendall (bubble sort) distance between two rank vectors
}

